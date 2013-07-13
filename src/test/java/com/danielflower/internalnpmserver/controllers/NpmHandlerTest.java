package com.danielflower.internalnpmserver.controllers;


import com.danielflower.internalnpmserver.services.FileDownloader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.File;
import java.net.URL;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class NpmHandlerTest {

    private final File cacheFolder = new File("target/npmcachetest/" + UUID.randomUUID() + "/");
    private final Mockery context = new JUnit4Mockery();
    private final FileDownloader proxyService = context.mock(FileDownloader.class);
    private final StaticHandler staticHandler = context.mock(StaticHandler.class);
    private final NpmHandler handler = new NpmHandler(proxyService, staticHandler, "http://registry.npmjs.org/", cacheFolder);
    private final Response response = context.mock(Response.class);
    private final Request request = context.mock(Request.class);

    @Test
    public void handlesCallsToAllUrlsStartingWithTextNPM() {
        assertThat(handler.canHandle("/npm"), is(true));
        assertThat(handler.canHandle("/npm/"), is(true));
        assertThat(handler.canHandle("/npm/some-module"), is(true));
        assertThat(handler.canHandle("/npm/commander/-/commander-0.6.1.tgz"), is(true));

        assertThat(handler.canHandle("/"), is(false));
        assertThat(handler.canHandle("/anythingelse"), is(false));
    }

    @Test
    public void usesTheStaticHandlerToStreamResponsesIfTheFileExistsAlready() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getTarget();
            will(returnValue("/npm/commander/-/commander-0.6.1.tgz"));
            allowing(staticHandler).canHandle("/commander/-/commander-0.6.1.tgz");
            will(returnValue(true));

            oneOf(staticHandler).streamFileToResponse("/commander/-/commander-0.6.1.tgz", response);
        }});
        handler.handle(request, response);
    }

    @Test
    public void usesJSONAsTheFileExtensionOfAPIRequests() throws Exception {
        runJsonRenameTest("commander/1.0.1");
    }

    @Test
    public void usesJSONAsTheFileExtensionOfAPIRequestsForHypenatedVersions() throws Exception {
        runJsonRenameTest("colors/0.6.0-1");
    }

    @Test
    public void usesJSONAsTheFileExtensionOfAPIRequestsForNumberedExtensions() throws Exception {
        runJsonRenameTest("colors/0.6.001");
    }

    private void runJsonRenameTest(final String path) throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getTarget();will(returnValue("/npm/" + path));
            allowing(staticHandler).canHandle("/" + path + ".json");will(returnValue(true));

            oneOf(staticHandler).streamFileToResponse("/" + path + ".json", response);
        }});
        handler.handle(request, response);
    }

    @Test
    public void downloadsFileBeforeSendingToStaticHandler() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getTarget(); will(returnValue("/npm/commander/-/commander-0.6.1.tgz"));

            atLeast(1).of(staticHandler).canHandle("/commander/-/commander-0.6.1.tgz");will(onConsecutiveCalls(returnValue(false), returnValue(true)));
            oneOf(proxyService).fetch(new URL("http://registry.npmjs.org/commander/-/commander-0.6.1.tgz"), new File(cacheFolder, "commander/-/commander-0.6.1.tgz"));
            oneOf(staticHandler).streamFileToResponse("/commander/-/commander-0.6.1.tgz", response);
        }});
        handler.handle(request, response);
    }

}

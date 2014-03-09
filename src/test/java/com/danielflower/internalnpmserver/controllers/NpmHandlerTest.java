package com.danielflower.internalnpmserver.controllers;


import com.danielflower.internalnpmserver.services.FileDownloader;
import com.danielflower.internalnpmserver.services.RemoteDownloadPolicy;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.File;
import java.io.IOException;
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
    private final RemoteDownloadPolicy remoteDownloadPolicy = context.mock(RemoteDownloadPolicy.class);
    private final NpmHandler handler = new NpmHandler(proxyService, staticHandler, "http://registry.npmjs.org/", cacheFolder, remoteDownloadPolicy);
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
	public void usesTheStaticHandlerToStreamResponsesWhenItIsAlreadyCached() throws Exception {
		context.checking(new Expectations() {{
			allowing(request).getTarget(); will(returnValue("/npm/commander/-/commander-0.6.1.tgz"));
			allowing(staticHandler).canHandle("/commander/-/commander-0.6.1.tgz"); will(returnValue(true));
			allowing(request).getValue("If-None-Match"); will(returnValue(""));

			oneOf(remoteDownloadPolicy).shouldDownload("/commander/-/commander-0.6.1.tgz"); will(returnValue(false));
			oneOf(staticHandler).streamFileToResponse("/commander/-/commander-0.6.1.tgz", "", response);
		}});
		handler.handle(request, response);
	}

	@Test
	public void passesTheETagFromTheRequestToTheHandler() throws Exception {
		context.checking(new Expectations() {{
			allowing(request).getTarget(); will(returnValue("/npm/commander/-/commander-0.6.1.tgz"));
			allowing(staticHandler).canHandle("/commander/-/commander-0.6.1.tgz"); will(returnValue(true));
			allowing(request).getValue("If-None-Match"); will(returnValue("12345"));
			allowing(remoteDownloadPolicy).shouldDownload("/commander/-/commander-0.6.1.tgz"); will(returnValue(false));

			oneOf(staticHandler).streamFileToResponse("/commander/-/commander-0.6.1.tgz", "12345", response);
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

    @Test
    public void usesJSONAsTheFileExtensionOfAPIRequestsForNamesWithDots() throws Exception {
        runJsonRenameTest("underscore.string");
    }

    private void runJsonRenameTest(final String path) throws Exception {
        final String expectedLocalPath = "/" + path + ".json";
        context.checking(new Expectations() {{
            allowing(request).getTarget();will(returnValue("/npm/" + path));
            allowing(remoteDownloadPolicy).shouldDownload(expectedLocalPath); will(returnValue(false));
            allowing(staticHandler).canHandle(expectedLocalPath);will(returnValue(true));
	        allowing(request).getValue("If-None-Match"); will(returnValue(""));

            oneOf(staticHandler).streamFileToResponse(expectedLocalPath, "", response);
        }});
        handler.handle(request, response);
    }

    @Test
    public void downloadsFileBeforeSendingToStaticHandlerIfThePolicySaysTo() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getTarget(); will(returnValue("/npm/commander/-/commander-0.6.1.tgz"));
	        allowing(request).getValue("If-None-Match"); will(returnValue(""));

            oneOf(remoteDownloadPolicy).shouldDownload("/commander/-/commander-0.6.1.tgz");will(returnValue(true));
            oneOf(proxyService).fetch(new URL("http://registry.npmjs.org/commander/-/commander-0.6.1.tgz"), new File(cacheFolder, "commander/-/commander-0.6.1.tgz"));
            oneOf(staticHandler).canHandle("/commander/-/commander-0.6.1.tgz");will(returnValue(true));
            oneOf(staticHandler).streamFileToResponse("/commander/-/commander-0.6.1.tgz", "", response);
        }});
        handler.handle(request, response);
    }

    @Test
    public void ifTheDownloadFailsThenTheStaticHandlerIsStillCalledIfThereIsACachedVersion() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getTarget(); will(returnValue("/npm/commander/-/commander-0.6.1.tgz"));
	        allowing(request).getValue("If-None-Match"); will(returnValue(""));

            oneOf(remoteDownloadPolicy).shouldDownload("/commander/-/commander-0.6.1.tgz");will(returnValue(true));
            oneOf(staticHandler).canHandle("/commander/-/commander-0.6.1.tgz");will(returnValue(true));

            oneOf(proxyService).fetch(with(any(URL.class)), with(any(File.class))); will(throwException(new RuntimeException("Simulated exception while making web request")));

            oneOf(staticHandler).streamFileToResponse("/commander/-/commander-0.6.1.tgz", "", response);
        }});
        handler.handle(request, response);
    }

    @Test(expected = IOException.class)
    public void ifTheDownloadFailsAndThereIsNoCachedVersionThenTheExceptionIsThrownUnmolested() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getTarget(); will(returnValue("/npm/commander/-/commander-0.6.1.tgz"));
	        allowing(request).getValue("If-None-Match"); will(returnValue(""));

	        oneOf(remoteDownloadPolicy).shouldDownload("/commander/-/commander-0.6.1.tgz");will(returnValue(true));
            oneOf(staticHandler).canHandle("/commander/-/commander-0.6.1.tgz");will(returnValue(false));
            oneOf(proxyService).fetch(with(any(URL.class)), with(any(File.class))); will(throwException(new IOException("Simulated exception while making web request")));
        }});
        handler.handle(request, response);
    }



}

package com.danielflower.internalnpmserver.controllers;


import com.danielflower.internalnpmserver.services.HttpProxyService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class NpmHandlerTest {

    private final Mockery context = new JUnit4Mockery();
    private final HttpProxyService proxyService = context.mock(HttpProxyService.class);
    private final NpmHandler handler = new NpmHandler(proxyService, "http://registry.npmjs.org/");
    private final Response response = context.mock(Response.class);
    private final Request request = context.mock(Request.class);

    @Test
    public void handlesCallsToAllUrlsStartingWithTextNPM() {
        assertThat(handler.canHandle("/npm"), is(true));
        assertThat(handler.canHandle("/npm/"), is(true));
        assertThat(handler.canHandle("/npm/some-module"), is(true));

        assertThat(handler.canHandle("/"), is(false));
        assertThat(handler.canHandle("/anythingelse"), is(false));
    }

    @Test
    public void proxiesRequestsToTheProxy() throws Exception {
        context.checking(new Expectations() {{
            allowing(request).getTarget(); will(returnValue("/npm/some-module"));

            oneOf(proxyService).proxy(new URL("http://registry.npmjs.org/some-module"), response);
        }});
        handler.handle(request, response);
    }

}

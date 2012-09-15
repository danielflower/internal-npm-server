package com.danielflower.webstarter.controllers;

import com.danielflower.webstarter.rendering.HttpViewRenderer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HomepageHandlerTest {

    private final Mockery context = new JUnit4Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final HttpViewRenderer viewRenderer = context.mock(HttpViewRenderer.class);
    private final HomepageHandler homepageHandler = new HomepageHandler(viewRenderer);

    @Test
    public void handlesCallsToRootOfWebsite() {
        assertThat(homepageHandler.canHandle("/"), is(true));
        assertThat(homepageHandler.canHandle("/anythingelse"), is(false));
    }

    @Test
    public void rendersTheHomePage() throws Exception {
        context.checking(new Expectations() {{
            oneOf(viewRenderer).render("home", null, response);
        }});
        homepageHandler.handle(request, response);
    }
}

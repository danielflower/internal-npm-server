package com.danielflower.internalnpmserver.controllers;

import com.danielflower.internalnpmserver.Config;
import com.danielflower.internalnpmserver.rendering.HttpViewRenderer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.File;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class HomepageHandlerTest {

    private final Mockery context = new JUnit4Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final HttpViewRenderer viewRenderer = context.mock(HttpViewRenderer.class);
    private final Config config = new Config(8080, new File("target"), "http://some.registry", "our.company.com", Proxy.NO_PROXY);
    private final HomepageHandler homepageHandler = new HomepageHandler(viewRenderer, config);

    @Test
    public void handlesCallsToRootOfWebsite() {
        assertThat(homepageHandler.canHandle("/"), is(true));
        assertThat(homepageHandler.canHandle("/anythingelse"), is(false));
    }

    @Test
    public void rendersTheHomePage() throws Exception {
        context.checking(new Expectations() {{
            oneOf(viewRenderer).render("home", mapWith("npmUrl", "http://our.company.com:8080/npm/"), response);
        }});
        homepageHandler.handle(request, response);
    }

    private Map<String, Object> mapWith(String key, String value) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put(key, value);
        return map;
    }
}

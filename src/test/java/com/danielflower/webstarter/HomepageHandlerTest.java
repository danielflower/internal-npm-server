package com.danielflower.webstarter;

import org.apache.commons.io.output.WriterOutputStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HomepageHandlerTest {

    private final Mockery context = new JUnit4Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final StringWriter responseContent = new StringWriter();
    private final WriterOutputStream outputStream = new WriterOutputStream(responseContent);
    private final HomepageHandler homepageHandler = new HomepageHandler();

    @Before
    public void setup() throws IOException {
        context.checking(new Expectations() {{
            allowing(response).getOutputStream(); will(returnValue(outputStream));
            allowing(response).close();
        }});
    }

    @Test
    public void handlesCallsToRootOfWebsite() {
        assertThat(homepageHandler.canHandle("/"), is(true));
        assertThat(homepageHandler.canHandle("/anythingelse"), is(false));
    }

    @Test
    public void sendsTheHomeViewToTheClient() throws Exception {
        context.checking(new Expectations() {{
            oneOf(response).set("Content-Type", "text/html");
            oneOf(response).close();
        }});
        homepageHandler.handle(request, response);
        assertThat(responseContent.toString(), containsString("<h2>Welcome</h2>"));
    }
}

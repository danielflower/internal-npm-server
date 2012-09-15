package org.simpleframework.http.core;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;

@RunWith(JMock.class)
public class LoggingWebContainerTest {
    private final Mockery context = new JUnit4Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final Container underlying = context.mock(Container.class);
    private final LoggingWebContainer loggingWebContainer = new LoggingWebContainer(underlying);

    @Before
    public void setup() throws IOException {
        context.checking(new Expectations() {{
            allowing(request).getTarget(); will(returnValue("/some/requested/path"));
        }});
    }

    @Test
    public void requestArePassedToTheUnderlyingContainer() throws Exception {
        context.checking(new Expectations() {{
            oneOf(underlying).handle(request, response);
        }});
        loggingWebContainer.handle(request, response);
    }
}

package com.danielflower.webstarter.webserver;

import org.apache.commons.io.output.WriterOutputStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class ErrorHandlingWebContainerTest {
    private final Mockery context = new JUnit4Mockery();
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final Container underlying = context.mock(Container.class);
    private final StringWriter responseContent = new StringWriter();
    private final PrintStream outputStream = new PrintStream(new WriterOutputStream(responseContent));
    private final ErrorHandlingWebContainer errorHandlingWebContainer = new ErrorHandlingWebContainer(underlying);

    @Before
    public void setup() throws IOException {
        context.checking(new Expectations() {{
            allowing(response).getPrintStream(); will(returnValue(outputStream));
            allowing(request).getTarget(); will(returnValue("/some/requested/path"));
        }});
    }

    @Test
    public void requestArePassedToTheUnderlyingContainer() throws Exception {
        context.checking(new Expectations() {{
            oneOf(underlying).handle(request, response);
        }});
        errorHandlingWebContainer.handle(request, response);
    }

    @Test
    public void notFoundExceptionsAreWrittenAs404Errors() throws Exception {
        context.checking(new Expectations() {{
            allowing(underlying).handle(request, response);
            will(throwException(new ResourceNotFoundException("/non/existent/path")));

            oneOf(response).setCode(404);
            oneOf(response).setText("404 Not Found");
            oneOf(response).close();
        }});
        errorHandlingWebContainer.handle(request, response);
        assertThat(responseContent.toString(), is("Sorry, could not find /non/existent/path"));
    }

    @Test
    public void unhandledExceptionsAreTreatedAs500ServerErrors() throws Exception {
        context.checking(new Expectations() {{
            allowing(underlying).handle(request, response);
            will(throwException(new RuntimeException("Some random exception")));

            oneOf(response).setCode(500);
            oneOf(response).setText("500 Internal Error");
            oneOf(response).close();
        }});
        errorHandlingWebContainer.handle(request, response);
        assertThat(responseContent.toString(), containsString("Oops, an error occurred"));
        assertThat(responseContent.toString(), containsString("Some random exception"));
    }
}

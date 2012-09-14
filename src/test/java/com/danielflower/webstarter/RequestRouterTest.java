package com.danielflower.webstarter;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

@RunWith(JMock.class)
public class RequestRouterTest {

    private final Mockery context = new JUnit4Mockery();
    private final RequestHandler unsupported = context.mock(RequestHandler.class, "unsupported");
    private final RequestHandler supported = context.mock(RequestHandler.class, "supported");
    private final String requestedPath = "/some/requested/path";
    private final Request request = context.mock(Request.class);
    private final Response response = context.mock(Response.class);
    private final Path path = context.mock(Path.class);

    @Before
    public void setup() {
        context.checking(new Expectations() {{
            allowing(request).getPath(); will(returnValue(path));
            allowing(path).getPath(); will(returnValue(requestedPath));
            allowing(request).getTarget(); will(returnValue(requestedPath));
        }});
    }

    @Test
    public void dispatchesRequestsToTheHandlerThatCanHandleIt() throws Exception {
        context.checking(new Expectations() {{
            exactly(2).of(unsupported).canHandle(requestedPath); will(returnValue(false));
            oneOf(supported).canHandle(requestedPath); will(returnValue(true));
            oneOf(supported).handle(request, response);
        }});

        RequestRouter container = new RequestRouter(new RequestHandler[] { unsupported, unsupported, supported, supported });
        container.handle(request, response);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenNoMatchingHandlersFoundAResourceNotFoundExceptionIsThrown() throws Exception {
        context.checking(new Expectations() {{
            exactly(2).of(unsupported).canHandle(requestedPath); will(returnValue(false));
        }});

        RequestRouter container = new RequestRouter(new RequestHandler[] { unsupported, unsupported });
        container.handle(request, response);
    }
}

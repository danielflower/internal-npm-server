package com.danielflower.internalnpmserver.rendering;

import org.apache.commons.io.output.WriterOutputStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleframework.http.Response;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@RunWith(JMock.class)
public class NonCachableHttpViewRendererTest {

    private final Mockery context = new JUnit4Mockery();
    private final ViewRenderer viewRenderer = context.mock(ViewRenderer.class);
    private final NonCachableHttpViewRenderer nonCachableHttpResponseViewRenderer = new NonCachableHttpViewRenderer(viewRenderer);
    private final String viewName = "viewname";
    private final Map<String,Object> model = new HashMap<String, Object>();
    private final Response response = context.mock(Response.class);
    private final StringWriter responseContent = new StringWriter();
    private final WriterOutputStream outputStream = new WriterOutputStream(responseContent);

    @Test
    public void rendersAViewUsingAViewRendererAndHeadersAreSetAppropriately() throws Exception {
        context.checking(new Expectations() {{
            oneOf(viewRenderer).render(with(viewName), with(same(model)), with(any(OutputStreamWriter.class)));
            // kinda lame way to "check" that the response output stream is used to write the view to
            oneOf(response).getOutputStream(); will(returnValue(outputStream));

            // check that response was correctly dealt with
            oneOf(response).addValue(with("Server"), with(any(String.class)));
            oneOf(response).addValue("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
            oneOf(response).addDate(with("Date"), with(any(long.class)));
            oneOf(response).addDate(with("Last-Modified"), with(any(long.class)));
            oneOf(response).addValue("Cache-Control", "no-store, no-cache, must-revalidate");
            oneOf(response).addValue("Cache-Control", "post-check=0, pre-check=0");
            oneOf(response).addValue("Pragma", "no-cache");
            oneOf(response).addValue("Content-Type", "text/html; charset=UTF-8");
            oneOf(response).close();
        }});
        nonCachableHttpResponseViewRenderer.render(viewName, model, response);
    }
}

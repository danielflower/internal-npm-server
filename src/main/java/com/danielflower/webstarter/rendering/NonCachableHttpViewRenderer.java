package com.danielflower.webstarter.rendering;

import com.danielflower.webstarter.webserver.ContentTypeGuesser;
import org.simpleframework.http.Response;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

public class NonCachableHttpViewRenderer implements HttpViewRenderer {

    private final ViewRenderer viewRenderer;

    public NonCachableHttpViewRenderer(ViewRenderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    @Override
    public void render(String viewName, Map<String, Object> model, Response response) throws IOException {
        response.add(ContentTypeGuesser.CONTENT_TYPE_HEADER, ContentTypeGuesser.TEXT_HTML + "; charset=UTF-8");
        response.add("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addDate("Date", System.currentTimeMillis());
        response.addDate("Last-Modified", System.currentTimeMillis());
        response.add("Cache-Control", "no-store, no-cache, must-revalidate");
        response.add("Cache-Control", "post-check=0, pre-check=0");
        response.add("Pragma", "no-cache");
        response.add("Server", "Simple-Java-Web-Starter");

        Writer writer = new OutputStreamWriter(response.getOutputStream());
        viewRenderer.render(viewName, model, writer);
		writer.close();
        response.close();
    }
}

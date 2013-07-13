package com.danielflower.internalnpmserver.rendering;

import org.simpleframework.http.Response;

import java.io.IOException;
import java.util.Map;

/**
 * An HTTP response wrapper around a ViewRenderer.
 */
public interface HttpViewRenderer {

    /**
     * Writes a view with model to the response, sets the correct headers, and closes the response.
     */
    void render(String viewName, Map<String, Object> model, Response response) throws IOException;
}

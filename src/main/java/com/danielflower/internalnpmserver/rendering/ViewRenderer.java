package com.danielflower.internalnpmserver.rendering;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface ViewRenderer {
    void render(String viewName, Map<String, Object> model, Writer writer) throws IOException;
}

package com.danielflower.webstarter.rendering;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class VelocityViewRenderer implements ViewRenderer {

    private final VelocityEngine velocityEngine = new VelocityEngine();
    private final String viewTemplateDirectory;

    public VelocityViewRenderer(String viewTemplateDirectory) {
        this.viewTemplateDirectory = viewTemplateDirectory;
        velocityEngine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, Slf4jLogChute.class.getCanonicalName());
        velocityEngine.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getCanonicalName());
        velocityEngine.init();
    }

    @Override
    public void render(String viewName, Map<String, Object> model, Writer writer) throws IOException {
        VelocityContext context = new VelocityContext(model);
        velocityEngine.mergeTemplate(viewTemplateDirectory + viewName + ".vm", "UTF-8", context, writer);
        writer.close();
    }
}

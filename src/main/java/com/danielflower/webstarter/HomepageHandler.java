package com.danielflower.webstarter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.OutputStreamWriter;
import java.io.Writer;

public class HomepageHandler implements RequestHandler {
    @Override
    public boolean canHandle(String path) {
        return "/".equals(path);
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        response.set("Content-Type", ContentTypeGuesser.text_html);

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, Slf4jLogChute.class.getCanonicalName());
        velocityEngine.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getCanonicalName());
        velocityEngine.init();

        VelocityContext context = new VelocityContext();
        Writer writer = new OutputStreamWriter(response.getOutputStream());
        velocityEngine.mergeTemplate("/views/home.vm", "UTF-8", context, writer);
        writer.close();
        response.close();
    }
}

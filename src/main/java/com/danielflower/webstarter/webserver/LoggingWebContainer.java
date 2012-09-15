package com.danielflower.webstarter.webserver;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingWebContainer implements Container {
    private static final Logger log = LoggerFactory.getLogger(LoggingWebContainer.class);
    private final Container underlying;

    public LoggingWebContainer(Container underlying) {
        this.underlying = underlying;
    }

    @Override
    public void handle(Request req, Response resp) {
        long start = System.currentTimeMillis();
        underlying.handle(req, resp);
        long time = System.currentTimeMillis() - start;
        log.info("Handled request to " + req.getTarget() + " in " + time + "ms");
    }
}

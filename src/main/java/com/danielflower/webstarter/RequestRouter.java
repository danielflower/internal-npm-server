package com.danielflower.webstarter;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

public class RequestRouter implements Container {
    private final RequestHandler[] handlers;

    public RequestRouter(RequestHandler[] handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handle(Request req, Response resp) {
        String path = req.getPath().getPath();
        for (RequestHandler handler : handlers) {
            if (handler.canHandle(path)) {
                try {
                    handler.handle(req, resp);
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new ResourceNotFoundException(req.getTarget());
    }
}

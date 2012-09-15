package com.danielflower.webstarter.controllers;

import com.danielflower.webstarter.rendering.HttpViewRenderer;
import com.danielflower.webstarter.webserver.RequestHandler;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class HomepageHandler implements RequestHandler {

    private final HttpViewRenderer viewRenderer;

    public HomepageHandler(HttpViewRenderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    @Override
    public boolean canHandle(String path) {
        return "/".equals(path);
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        viewRenderer.render("home", null, response);
    }
}

package com.danielflower.internalnpmserver.controllers;

import com.danielflower.internalnpmserver.Config;
import com.danielflower.internalnpmserver.rendering.HttpViewRenderer;
import com.danielflower.internalnpmserver.webserver.RequestHandler;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.util.HashMap;
import java.util.Map;

public class HomepageHandler implements RequestHandler {

    private final HttpViewRenderer viewRenderer;
    private final Config config;

    public HomepageHandler(HttpViewRenderer viewRenderer, Config config) {
        this.viewRenderer = viewRenderer;
        this.config = config;
    }

    @Override
    public boolean canHandle(String path) {
        return "/".equals(path);
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("npmUrl", config.getNpmEndPoint().toString());
        viewRenderer.render("home", model, response);
    }
}

package com.danielflower.internalnpmserver.controllers;

import com.danielflower.internalnpmserver.rendering.HttpViewRenderer;
import com.danielflower.internalnpmserver.services.HttpProxyService;
import com.danielflower.internalnpmserver.webserver.RequestHandler;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class NpmHandler implements RequestHandler {


    public NpmHandler(HttpProxyService proxyService) {
    }

    @Override
    public boolean canHandle(String path) {
        return path.startsWith("/npm");
    }

    @Override
    public void handle(Request request, Response response) throws Exception {

    }
}

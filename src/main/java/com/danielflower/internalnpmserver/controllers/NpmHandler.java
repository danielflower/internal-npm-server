package com.danielflower.internalnpmserver.controllers;

import com.danielflower.internalnpmserver.services.HttpProxyService;
import com.danielflower.internalnpmserver.webserver.RequestHandler;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.net.URL;

public class NpmHandler implements RequestHandler {


    private final HttpProxyService proxyService;
    private final String npmRepositoryURL;

    public NpmHandler(HttpProxyService proxyService, String npmRepositoryURL) {
        this.proxyService = proxyService;
        this.npmRepositoryURL = npmRepositoryURL;
    }

    @Override
    public boolean canHandle(String path) {
        return path.startsWith("/npm");
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        String remotePath = request.getTarget().substring(npmRepositoryURL.endsWith("/") ? 5 : 4);
        URL url = new URL(npmRepositoryURL + remotePath);
        proxyService.proxy(url, response);
    }
}

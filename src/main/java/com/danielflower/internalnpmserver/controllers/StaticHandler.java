package com.danielflower.internalnpmserver.controllers;

import org.simpleframework.http.Response;

import java.io.IOException;
import java.util.Date;

public interface StaticHandler {
    boolean canHandle(String path);

    void streamFileToResponse(String path, Response resp) throws IOException;

	Date dateCreated(String path);
}

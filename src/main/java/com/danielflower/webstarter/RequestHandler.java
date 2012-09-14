package com.danielflower.webstarter;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public interface RequestHandler {

    boolean canHandle(String path);

    void handle(Request request, Response response) throws Exception;

}

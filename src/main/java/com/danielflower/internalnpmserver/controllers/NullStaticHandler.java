package com.danielflower.internalnpmserver.controllers;

import com.danielflower.internalnpmserver.webserver.RequestHandler;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Date;

/**
 * A static handler that does nothing. Use when you need a null handler without having to explicitly handle nulls.
 */
public class NullStaticHandler implements StaticHandler, RequestHandler {
	@Override
	public boolean canHandle(String path) {
		return false;
	}

	@Override
	public void handle(Request request, Response response) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void streamFileToResponse(String path, Response resp) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public Date dateCreated(String path) {
		throw new NotImplementedException();
	}
}

package com.danielflower.internalnpmserver.services;

import org.simpleframework.http.Response;

import java.io.IOException;
import java.net.URL;

public interface HttpProxyService {

    void proxy(URL url, Response response) throws IOException;

}

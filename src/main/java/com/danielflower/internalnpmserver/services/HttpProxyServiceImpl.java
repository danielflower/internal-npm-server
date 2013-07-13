package com.danielflower.internalnpmserver.services;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Response;

import java.io.*;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class HttpProxyServiceImpl implements HttpProxyService {
    @Override
    public void proxy(URL url, Response response) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setDoInput(true);

        InputStream inputStream = conn.getInputStream();
        OutputStream outputStream = response.getOutputStream();
        try {
            IOUtils.copy(inputStream, outputStream);

            response.setValue("Content-Type", conn.getHeaderField("Content-Type"));

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }

    }
}

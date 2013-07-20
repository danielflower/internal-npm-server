package com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.webserver.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class FileDownloaderImpl implements FileDownloader {
    private static final Logger log = LoggerFactory.getLogger(FileDownloaderImpl.class);

    private final Proxy proxy;

    public FileDownloaderImpl(Proxy proxy) {
        this.proxy = proxy;
        HttpURLConnection.setFollowRedirects(true);
    }

    @Override
    public void fetch(URL source, File destination) throws IOException {
        if (destination.getParentFile().mkdirs()) {
            log.info("Will create " + destination.getCanonicalPath());
        }
        HttpURLConnection conn = (HttpURLConnection) source.openConnection(proxy);
        conn.setDoInput(true);
        conn.setInstanceFollowRedirects(true);
        conn.connect();
        int code = conn.getResponseCode();
        if (code >= 300 && code < 400) {
            // HttpURLConnection.setFollowRedirects(true); didn't seem to work, so this:
            String location = conn.getHeaderField("Location");
            log.info("Got " + code + " with location " + location);
            fetch(new URL(location), destination);
        } else {

            InputStream inputStream;
            try {
                inputStream = conn.getInputStream();
            } catch (FileNotFoundException e) {
                throw new ResourceNotFoundException(source.toString());
            }
            OutputStream outputStream = new FileOutputStream(destination, false);

            try {
                int bytesDownloaded = IOUtils.copy(inputStream, outputStream);
                log.info("Downloaded " + source + " (" + bytesDownloaded + " bytes)");
            } finally {
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
            }
        }
    }
}

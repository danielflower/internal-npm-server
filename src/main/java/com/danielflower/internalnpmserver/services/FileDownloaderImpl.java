package com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.webserver.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloaderImpl implements FileDownloader {
    private static final Logger log = LoggerFactory.getLogger(FileDownloaderImpl.class);

    private final Proxy proxy;

    public FileDownloaderImpl(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void fetch(URL source, File destination) throws IOException {
        if (destination.getParentFile().mkdirs()) {
            log.info("Will create " + destination.getCanonicalPath());
        }

        URLConnection conn = source.openConnection(proxy);
        conn.setDoInput(true);

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

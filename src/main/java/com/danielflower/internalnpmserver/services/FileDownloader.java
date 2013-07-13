package com.danielflower.internalnpmserver.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public interface FileDownloader {

    void fetch(URL source, File destination) throws IOException;

}

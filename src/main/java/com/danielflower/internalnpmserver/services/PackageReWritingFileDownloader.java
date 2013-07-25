package com.danielflower.internalnpmserver.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PackageReWritingFileDownloader implements FileDownloader {

    private final FileDownloader underlying;
    private final String externalHTTPNPMRegistry;
    private final String internalNPMRegistry;
    private final String externalHTTPSNPMRegistry;

    public PackageReWritingFileDownloader(FileDownloader underlying, String externalNPMRegistry, String internalNPMRegistry) {
        this.underlying = underlying;
        externalNPMRegistry = StringUtils.stripEnd(externalNPMRegistry, "/");
        internalNPMRegistry = StringUtils.stripEnd(internalNPMRegistry, "/");
        this.externalHTTPNPMRegistry = externalNPMRegistry.replace("https://", "http://");
        this.externalHTTPSNPMRegistry = externalNPMRegistry.replace("http://", "https://");
        this.internalNPMRegistry = internalNPMRegistry;
    }

    @Override
    public void fetch(URL source, File destination) throws IOException {
        underlying.fetch(source, destination);

        if (FilenameUtils.getExtension(destination.getName()).equalsIgnoreCase("json")) {
            String contents = FileUtils.readFileToString(destination);
            if (true || contents.contains(externalHTTPNPMRegistry) || contents.contains(externalHTTPSNPMRegistry)) {
                contents = contents.replace(externalHTTPNPMRegistry, internalNPMRegistry);
                contents = contents.replace(externalHTTPSNPMRegistry, internalNPMRegistry);
                contents = contents.replaceAll("\"shasum\"", "\"originalshasum\"");
                FileUtils.write(destination, contents);
            }
        }
    }
}

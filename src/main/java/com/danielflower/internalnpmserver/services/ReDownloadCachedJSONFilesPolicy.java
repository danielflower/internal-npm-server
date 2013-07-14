package com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.controllers.StaticHandler;
import org.apache.commons.io.FilenameUtils;

public class ReDownloadCachedJSONFilesPolicy implements RemoteDownloadPolicy {
    private final StaticHandler staticHandler;

    public ReDownloadCachedJSONFilesPolicy(StaticHandler staticHandler) {
        this.staticHandler = staticHandler;
    }

    @Override
    public boolean shouldDownload(String localPath) {
        boolean isCached = staticHandler.canHandle(localPath);

        return !isCached || FilenameUtils.getExtension(localPath).equalsIgnoreCase("json");
    }
}

package com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.controllers.StaticHandler;
import org.apache.commons.io.FilenameUtils;

public class ReDownloadCachedJSONFilesPolicy implements RemoteDownloadPolicy {
	private final StaticHandler internalRepositoryHolderHandler;
	private final StaticHandler staticHandler;

    public ReDownloadCachedJSONFilesPolicy(StaticHandler internalRepositoryHolderHandler, StaticHandler remoteCacheHandler) {
	    this.internalRepositoryHolderHandler = internalRepositoryHolderHandler;
	    this.staticHandler = remoteCacheHandler;
    }

    @Override
    public boolean shouldDownload(String localPath) {
	    if (internalRepositoryHolderHandler.canHandle(localPath)) {
		    return false;
	    }

        boolean isCached = staticHandler.canHandle(localPath);
        return !isCached || FilenameUtils.getExtension(localPath).equalsIgnoreCase("json");
    }
}

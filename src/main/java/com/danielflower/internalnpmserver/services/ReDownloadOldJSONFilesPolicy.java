package com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.controllers.StaticHandler;
import org.apache.commons.io.FilenameUtils;

import java.util.Date;

public class ReDownloadOldJSONFilesPolicy implements RemoteDownloadPolicy {
	private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 24 * 60 * 60 * 1000;
	private final StaticHandler internalRepositoryHolderHandler;
	private final StaticHandler remoteCacheHandler;

	public ReDownloadOldJSONFilesPolicy(StaticHandler internalRepositoryHolderHandler, StaticHandler remoteCacheHandler) {
		this.internalRepositoryHolderHandler = internalRepositoryHolderHandler;
		this.remoteCacheHandler = remoteCacheHandler;
	}

	@Override
	public boolean shouldDownload(String localPath) {
		if (internalRepositoryHolderHandler.canHandle(localPath)) {
			return false;
		}
		boolean existsLocally = remoteCacheHandler.canHandle(localPath);
		boolean isJSONFile = FilenameUtils.getExtension(localPath).equalsIgnoreCase("json");
		if (existsLocally && !isJSONFile) {
			return false;
		}
		if (!existsLocally) {
			return true;
		}
		Date fileModifiedDate = remoteCacheHandler.dateCreated(localPath);
		long age = System.currentTimeMillis() - fileModifiedDate.getTime();
		return age > TWENTY_FOUR_HOURS_IN_MILLIS;
	}
}

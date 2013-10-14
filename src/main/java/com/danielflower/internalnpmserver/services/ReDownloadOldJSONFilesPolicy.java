package com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.controllers.StaticHandler;
import org.apache.commons.io.FilenameUtils;

import java.util.Date;

public class ReDownloadOldJSONFilesPolicy implements RemoteDownloadPolicy {
	private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 24 * 60 * 60 * 1000;
    private final StaticHandler staticHandler;

    public ReDownloadOldJSONFilesPolicy(StaticHandler staticHandler) {
        this.staticHandler = staticHandler;
    }

	@Override
	public boolean shouldDownload(String localPath) {
		boolean existsLocally = staticHandler.canHandle(localPath);
		boolean isJSONFile = FilenameUtils.getExtension(localPath).equalsIgnoreCase("json");
		if (existsLocally && !isJSONFile) {
			return false;
		}
		if (!existsLocally) {
			return true;
		}
		Date fileModifiedDate = staticHandler.dateCreated(localPath);
		long age = System.currentTimeMillis() - fileModifiedDate.getTime();
		return age > TWENTY_FOUR_HOURS_IN_MILLIS;
	}
}

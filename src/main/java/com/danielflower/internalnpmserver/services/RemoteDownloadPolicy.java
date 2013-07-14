package com.danielflower.internalnpmserver.services;

public interface RemoteDownloadPolicy {
    boolean shouldDownload(String localPath);
}

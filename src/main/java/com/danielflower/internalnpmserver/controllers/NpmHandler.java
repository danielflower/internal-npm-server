package com.danielflower.internalnpmserver.controllers;

import com.danielflower.internalnpmserver.services.FileDownloader;
import com.danielflower.internalnpmserver.services.RemoteDownloadPolicy;
import com.danielflower.internalnpmserver.webserver.RequestHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NpmHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(NpmHandler.class);


    private static final Pattern extensionPattern = Pattern.compile("[A-Za-z]+");

    public static final String PREFIX = "/npm";
    private final FileDownloader proxyService;
    private final StaticHandler staticHandler;
    private final String npmRepositoryURL;
    private final File cacheFolder;
    private final RemoteDownloadPolicy remoteDownloadPolicy;

    public NpmHandler(FileDownloader proxyService, StaticHandler staticHandler, String npmRepositoryURL, File cacheFolder, RemoteDownloadPolicy remoteDownloadPolicy) {
        this.proxyService = proxyService;
        this.staticHandler = staticHandler;
        this.remoteDownloadPolicy = remoteDownloadPolicy;
        this.npmRepositoryURL = StringUtils.stripEnd(npmRepositoryURL, "/");

        this.cacheFolder = cacheFolder;
    }

    @Override
    public boolean canHandle(String path) {
        return path.startsWith(PREFIX);
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        String remotePath = request.getTarget().substring(PREFIX.length());

        String localPath = getLocalPathTreatingExtensionlessFilesAsJSONFiles(remotePath);

        if (remoteDownloadPolicy.shouldDownload(localPath)) {
            URL source = new URL(npmRepositoryURL + remotePath);
            try {
                proxyService.fetch(source, new File(cacheFolder, localPath));
            } catch (Exception e) {
                if (staticHandler.canHandle(localPath)) {
                    log.warn("Failed to download " + source + " but it's not a huge problem as the local cached copy can be used. Error was: " + e.getMessage());
                    staticHandler.streamFileToResponse(localPath, response);
                    return;
                } else {
                    throw e;
                }

            }
        }

        if (staticHandler.canHandle(localPath)) {
            staticHandler.streamFileToResponse(localPath, response);
        }
    }

    private String getLocalPathTreatingExtensionlessFilesAsJSONFiles(String path) {
        String ext = FilenameUtils.getExtension(path);
        Matcher matcher = extensionPattern.matcher(ext);
        if (!matcher.matches()) {
            path += ".json";
        }
        return path;
    }
}

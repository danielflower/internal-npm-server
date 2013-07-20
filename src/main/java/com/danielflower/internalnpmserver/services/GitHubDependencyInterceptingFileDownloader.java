package com.danielflower.internalnpmserver.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubDependencyInterceptingFileDownloader implements FileDownloader {
    private static final Logger log = LoggerFactory.getLogger(GitHubDependencyInterceptingFileDownloader.class);

    private final FileDownloader underlying;
    private final File npmCacheFolder;
    private final Pattern gitHubPattern = Pattern.compile(".*\"([A-Za-z0-9._-]+)\"\\s*:\\s*\"(git\\+https://github\\.com/([A-Za-z0-9_-]+)/([A-Za-z0-9_\\-]+)(\\.git)?(#)?([A-Za-z0-9._-]*))\".*");

    public GitHubDependencyInterceptingFileDownloader(File npmCacheFolder, FileDownloader underlying) {
        this.underlying = underlying;
        this.npmCacheFolder = npmCacheFolder;
    }

    @Override
    public void fetch(URL source, File destination) throws IOException {
        underlying.fetch(source, destination);

        if (FilenameUtils.getExtension(destination.getName()).equalsIgnoreCase("json")) {
            String contents = FileUtils.readFileToString(destination);
            if (contents.contains("git+https://github.com/")) {
                Matcher matcher = gitHubPattern.matcher(contents);
                List<Dependency> dependencies = new ArrayList<Dependency>();
                while (matcher.find()) {
                    Dependency dep = new Dependency();
                    dep.npmModuleName = matcher.group(1);
                    dep.gitHubAuthor = matcher.group(3);
                    dep.gitHubRepoName = matcher.group(4);
                    dep.gitRef = StringUtils.isBlank(matcher.group(7)) ? "master" : matcher.group(7);
                    dep.fullDependencyReference = matcher.group(2);
                    dependencies.add(dep);
                }

                for (Dependency dependency : dependencies) {
                    log.info("Replacing GitHub dependency: " + dependency);
                    contents = contents.replace(dependency.fullDependencyReference, dependency.gitRef);

                    String localPath = dependency.npmModuleName + "/-/" + dependency.npmModuleName + "-" + dependency.gitRef + ".tgz";
                    File depDestination = new File(npmCacheFolder, localPath);
                    if (!depDestination.isFile()) {
                        URL tarballURL = dependency.getTarballURL();
                        log.info("Will download " + tarballURL + " to " + depDestination);
                        // assumption: this is the outer-most FileDownloader implementation
                        fetch(tarballURL, depDestination);
                    }
                }

                FileUtils.write(destination, contents);
            }
        }
    }

    private static class Dependency {
        public String npmModuleName;
        public String gitHubAuthor;
        public String gitHubRepoName;
        public String gitRef;
        public String fullDependencyReference;

        @Override
        public String toString() {
            return "{" +
                    "npmModuleName='" + npmModuleName + '\'' +
                    ", gitHubAuthor='" + gitHubAuthor + '\'' +
                    ", gitHubRepoName='" + gitHubRepoName + '\'' +
                    ", gitRef='" + gitRef + '\'' +
                    ", fullDependencyReference='" + fullDependencyReference + '\'' +
                    '}';
        }

        public URL getTarballURL() {
            String url = "http://github.com/" + gitHubAuthor + "/" + gitHubRepoName + "/tarball/" + gitRef;
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                log.error("Error making URL " + url + " for dependency " + this, e);
                throw new RuntimeException(e);
            }
        }
    }
}

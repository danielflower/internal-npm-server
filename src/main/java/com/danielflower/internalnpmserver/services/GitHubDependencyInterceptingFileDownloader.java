package com.danielflower.internalnpmserver.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubDependencyInterceptingFileDownloader implements FileDownloader {
    private static final Logger log = LoggerFactory.getLogger(GitHubDependencyInterceptingFileDownloader.class);

    private final FileDownloader underlying;
    private final File npmCacheFolder;
    private final ModuleRewriter moduleRewriter;
    private static final Pattern gitHubPattern = Pattern.compile(".*\"([A-Za-z0-9._-]+)\"\\s*:\\s*\"(git\\+https://github\\.com/([A-Za-z0-9_-]+)/([A-Za-z0-9_\\-]+)(\\.git)?(#)?([A-Za-z0-9._-]*))\".*");

    public GitHubDependencyInterceptingFileDownloader(File npmCacheFolder, FileDownloader underlying, ModuleRewriter moduleRewriter) {
        this.underlying = underlying;
        this.npmCacheFolder = npmCacheFolder;
        this.moduleRewriter = moduleRewriter;
    }

    @Override
    public void fetch(URL source, File destination) throws IOException {
        boolean didExist = destination.isFile();
        underlying.fetch(source, destination);


        if (FilenameUtils.getExtension(destination.getName()).equalsIgnoreCase("json")) {
            List<Dependency> dependencies = updateDependenciesInFile(destination);
            for (Dependency dependency : dependencies) {
                String localPath = dependency.npmModuleName + "/-/" + dependency.npmModuleName + "-" + dependency.gitRef + ".tgz";
                File depDestination = new File(npmCacheFolder, localPath);
                if (!depDestination.isFile()) {
                    URL tarballURL = dependency.getTarballURL();
                    log.info("Will download " + tarballURL + " to " + depDestination);
                    // assumption: this is the outer-most FileDownloader implementation
                    fetch(tarballURL, depDestination);
                }
            }
        } else if (!didExist) {
            moduleRewriter.rewriteModule(destination);
        }
    }

    public static List<Dependency> updateDependenciesInFile(File destination) throws IOException {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        String contents = FileUtils.readFileToString(destination);
        if (contents.contains("git+https://github.com/")) {
            Matcher matcher = gitHubPattern.matcher(contents);

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
            }

            FileUtils.write(destination, contents);
        }
        return dependencies;
    }

}

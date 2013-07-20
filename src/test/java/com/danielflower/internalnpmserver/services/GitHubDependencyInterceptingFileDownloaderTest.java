package com.danielflower.internalnpmserver.services;

import org.apache.commons.io.FileUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class GitHubDependencyInterceptingFileDownloaderTest {

    private final Mockery context = new JUnit4Mockery();
    private final FileDownloader underlying = context.mock(FileDownloader.class);
    private final File npmCacheFolder = new File("target/" + UUID.randomUUID());
    private final GitHubDependencyInterceptingFileDownloader downloader = new GitHubDependencyInterceptingFileDownloader(npmCacheFolder, underlying);

    @Test
    public void nonJSONRequestsArePassedToUnderlyingDownloadedUnmolested() throws IOException {
        final URL source = new URL("http://example.org/whatever/1.0.0");
        final File destination = new File("target/not-a-json-file.zip");

        context.checking(new Expectations() {{
            oneOf(underlying).fetch(source, destination);
        }});

        downloader.fetch(source, destination);
    }

    @Test
    public void gitHubDependenciesAreDownloadedAndReWrittenAsNormalDependencies() throws IOException {
        final URL source = new URL("http://example.org/whatever/1.0.0");
        final File destination = putJSONFileInCache();

        context.checking(new Expectations() {{
            // the first one
            oneOf(underlying).fetch(source, destination);

            // recursively
            oneOf(underlying).fetch(new URL("http://github.com/jsdoc3/jsdoc/tarball/v3.2.0"), new File(npmCacheFolder, "jsdoc3/-/jsdoc3-v3.2.0.tgz"));
            oneOf(underlying).fetch(new URL("http://github.com/terryweiss/docstrap/tarball/master"), new File(npmCacheFolder, "docstrap/-/docstrap-master.tgz"));
            oneOf(underlying).fetch(new URL("http://github.com/dominictarr/crypto-browserify/tarball/95c5d505"), new File(npmCacheFolder, "crypto-browserify/-/crypto-browserify-95c5d505.tgz"));
            oneOf(underlying).fetch(new URL("http://github.com/jsdoc3/markdown-js/tarball/master"), new File(npmCacheFolder, "markdown/-/markdown-master.tgz"));
            oneOf(underlying).fetch(new URL("http://github.com/hegemonic/taffydb/tarball/1.0.1"), new File(npmCacheFolder, "taffydb/-/taffydb-1.0.1.tgz"));
        }});

        downloader.fetch(source, destination);

        String result = FileUtils.readFileToString(destination);
        assertThat(result, not(containsString("git+https://github.com/")));
        assertThat(result, not(containsString("git+http://github.com/")));
        assertThat(result, containsString("\"jsdoc3\": \"v3.2.0\""));
        assertThat(result, containsString("\"docstrap\": \"master\""));
        assertThat(result, containsString("\"crypto-browserify\": \"95c5d505\""));
        assertThat(result, containsString("\"markdown\": \"master\""));
        assertThat(result, containsString("\"taffydb\": \"1.0.1\""));
    }

    private File putJSONFileInCache() throws IOException {
        File jsonFile = new File("src/test/resources/packageWithGitHubDependencies.json");
        if (!jsonFile.isFile()) {
            throw new RuntimeException("Really need " + jsonFile.getCanonicalPath() + " to exist for this test");
        }
        File target = new File("target");
        FileUtils.copyFileToDirectory(jsonFile, target);
        return new File(target, "/packageWithGitHubDependencies.json");
    }

}

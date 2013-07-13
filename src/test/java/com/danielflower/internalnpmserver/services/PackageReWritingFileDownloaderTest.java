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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class PackageReWritingFileDownloaderTest {

    private final Mockery context = new JUnit4Mockery();
    private final FileDownloader underlying = context.mock(FileDownloader.class);
    private final PackageReWritingFileDownloader downloader = new PackageReWritingFileDownloader(underlying,
            "http://registry.npmjs.org/", "http://localhost:9100/npm/");

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
    public void jsonRequestsAreResavedToRemoveReferencesToExternalNPMServer() throws IOException {
        final URL source = new URL("http://example.org/whatever/1.0.0");
        final File destination = putJSONFileInCache();

        context.checking(new Expectations() {{
            oneOf(underlying).fetch(source, destination);
        }});

        downloader.fetch(source, destination);

        String result = FileUtils.readFileToString(destination);
        assertThat(result, not(containsString("registry.npmjs.org")));
        assertThat(result, containsString("\"tarball\": \"http://localhost:9100/npm/colors/-/colors-0.3.0.tgz\""));
        assertThat(result, containsString("\"tarball\": \"http://localhost:9100/npm/colors/-/colors-0.5.0.tgz\""));
    }

    private File putJSONFileInCache() throws IOException {
        File jsonFile = new File("src/test/resources/packageWithExternalRepos.json");
        if (!jsonFile.isFile()) {
            throw new RuntimeException("Really need " + jsonFile.getCanonicalPath() + " to exist for this test");
        }
        File target = new File("target");
        FileUtils.copyFileToDirectory(jsonFile, target);
        return new File(target, "/packageWithExternalRepos.json");
    }

}

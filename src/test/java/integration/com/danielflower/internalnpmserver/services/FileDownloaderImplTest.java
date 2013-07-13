package integration.com.danielflower.internalnpmserver.services;

import com.danielflower.internalnpmserver.services.FileDownloaderImpl;
import com.danielflower.internalnpmserver.webserver.ResourceNotFoundException;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FileDownloaderImplTest {

    private final FileDownloaderImpl downloader = new FileDownloaderImpl();

    @Test
    public void downloadsFromAURLToAFile() throws Exception {
        URL url = new URL("http://registry.npmjs.org/grunt-contrib-jshint/0.6.0");

        File destination = new File("target/npmcachetest/" + UUID.randomUUID() + "/0.6.0.json");
        downloader.fetch(url, destination);
        assertThat(destination.isFile(), is(true));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void resourceNotFoundExceptionsThrownFor404Errors() throws Exception {
        URL url = new URL("http://registry.npmjs.org/idontexist/not/here/or/anywhere");

        File destination = new File("target/npmcachetest/" + UUID.randomUUID() + "/0.6.0.json");
        downloader.fetch(url, destination);
    }

}

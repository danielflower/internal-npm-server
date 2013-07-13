package com.danielflower.internalnpmserver.webserver;

import com.danielflower.internalnpmserver.controllers.StaticHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ContentTypeGuesserTest {

    private final ContentTypeGuesser contentTypeGuesser = new ContentTypeGuesser();

    @Test
    public void returnsMimeTypeBasedOnFileExtension() throws Exception {
        assertThat(contentTypeGuesser.fromName("/some/path.html"), is("text/html"));
    }

    @Test
    public void returnsEmptyStringForUnknownTypes() throws Exception {
        assertThat(contentTypeGuesser.fromName("/some/path.bleurgh"), is(""));
    }

    @Test
    public void returnsEmptyStringForFilesWithoutExtensions() throws Exception {
        assertThat(contentTypeGuesser.fromName("/some/path"), is(""));
    }

    @Test
    public void everyStaticResourceHasMimeTypeSet() throws IOException {

        Collection<File> files = FileUtils.listFiles(StaticHandler.webroot, null, true);
        for (File file : files) {
            String path = file.getCanonicalPath();
            assertThat("Mime type of " + path, contentTypeGuesser.fromName(path), is(not("")));
        }

    }

}

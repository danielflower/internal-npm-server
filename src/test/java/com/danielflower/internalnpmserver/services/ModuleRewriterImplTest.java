package com.danielflower.internalnpmserver.services;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ModuleRewriterImplTest {

    private File archive;
    private final ModuleRewriterImpl moduleRewriter = new ModuleRewriterImpl();

    @Before
    public void putSampleArchiveInTempFolder() throws IOException {
        File file = new File("src/test/resources/samplecache/jsdoc/-/jsdoc3-cfff8dd035ad376892139192c03718ce2dcc20f0.tgz");
        if (!file.isFile()) {
            throw new RuntimeException("Really need " + file.getCanonicalPath() + " to exist for this test");
        }
        File target = new File("target/" + UUID.randomUUID() + "/-/");
        FileUtils.copyFileToDirectory(file, target);
        archive = new File(target, "jsdoc3-cfff8dd035ad376892139192c03718ce2dcc20f0.tgz");
    }

    @Test
    public void unpacksAndUpdatesThePackageJsonFileAndRepacks() throws IOException {
        moduleRewriter.rewriteModule(archive);

        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        File locationOfExtractFiles = new File(archive.getParentFile(), "actual");
        archiver.extract(archive, locationOfExtractFiles);
        File packageFile = new File(locationOfExtractFiles, "package.json");
        assertThat(packageFile.isFile(), is(true));

        String contents = FileUtils.readFileToString(packageFile);
        assertThat(contents, not(containsString("git+https://github.com/")));
        assertThat(contents, containsString("\"crypto-browserify\": \"95c5d505\""));
        assertThat(contents, containsString("\"github-flavored-markdown\": \"master\""));
        assertThat(contents, containsString("\"markdown\": \"master\""));
        assertThat(contents, containsString("\"taffydb\":  \"master\""));

    }

}

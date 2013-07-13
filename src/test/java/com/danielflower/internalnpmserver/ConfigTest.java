package com.danielflower.internalnpmserver;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ConfigTest {

    private final File npmCacheFolder = new File("target/whatever");

    @Test(expected = RuntimeException.class)
    public void httpsIsNotAllowedForNPMRegistryURL() {
        new Config(1000, npmCacheFolder, "https://www.blah.com");
    }

    @Test(expected = RuntimeException.class)
    public void schemeNameMustBeIncluded() {
        new Config(1000, npmCacheFolder, "www.blah.com");
    }

    @Test
    public void httpSchemeIsAcceptable() {
        new Config(1000, npmCacheFolder, "http://registry.npmjs.org/");
    }

    @Test
    public void configCanBeReadyFromPropertiesFile() {
        Config config = Config.fromFile("src/test/resources/sample-config.properties");
        assertThat(config.getPort(), equalTo(1234));
        assertThat(config.getNpmCacheFolder(), equalTo(new File("target/some/folder")));
        assertThat(config.getNpmRepositoryURL(), equalTo("http://registry.npmjs.org/"));

    }

    @Test(expected = RuntimeException.class)
    public void throwsIfConfigFileNotFound() {
        Config.fromFile("src/test/resources/i-do-not-exist.properties");
    }

}

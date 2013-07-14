package com.danielflower.internalnpmserver;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.net.Proxy;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ConfigTest {

    private final File npmCacheFolder = new File("target/whatever");
    private String webServerHostName = "localhost";

    @Test(expected = RuntimeException.class)
    public void httpsIsNotAllowedForNPMRegistryURL() {
        new Config(1000, npmCacheFolder, "https://www.blah.com", webServerHostName, null);
    }

    @Test(expected = RuntimeException.class)
    public void schemeNameMustBeIncluded() {
        new Config(1000, npmCacheFolder, "www.blah.com", webServerHostName, null);
    }

    @Test
    public void httpSchemeIsAcceptable() {
        new Config(1000, npmCacheFolder, "http://registry.npmjs.org/", webServerHostName, null);
    }

    @Test
    public void configCanBeReadyFromPropertiesFile() {
        Config config = Config.fromFile("src/test/resources/configs/sample-config.properties");
        assertThat(config.getPort(), equalTo(1234));
        assertThat(config.getNpmCacheFolder(), equalTo(new File("target/some/folder")));
        assertThat(config.getNpmRepositoryURL(), equalTo("http://registry.npmjs.org/"));
        assertThat(config.getWebServerHostName(), equalTo("localhost"));
    }

    @Test(expected = RuntimeException.class)
    public void throwsIfConfigFileNotFound() {
        Config.fromFile("src/test/resources/i-do-not-exist.properties");
    }

    @Test
    public void ifNoProxyHostSetThenNoProxyIsSet() {
        Config config = Config.fromFile("src/test/resources/configs/no-proxy.properties");
        assertThat(config.getProxy(), is(Matchers.equalTo(Proxy.NO_PROXY)));
    }

    @Test
    public void createsAnHTTPProxyWithHostAndPort() throws NoSuchFieldException, IllegalAccessException {
        Config config = Config.fromFile("src/test/resources/configs/non-authenticated-proxy.properties");
        Proxy proxy = config.getProxy();
        assertThat(proxy.type(), equalTo(Proxy.Type.HTTP));
        assertThat(proxy.address().toString(), equalTo("some.proxy.com:8080"));
    }

    @Test
    public void setsDefaultAuthenticatorIfUsernameAndPasswordSet() {
        Config config = Config.fromFile("src/test/resources/configs/authenticated-proxy.properties");
        Proxy proxy = config.getProxy();
        assertThat(proxy.type(), equalTo(Proxy.Type.HTTP));
        assertThat(proxy.address().toString(), equalTo("some.proxy.com:8080"));
        // okay... how to test and authenticator has been set?!?
    }
}

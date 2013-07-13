package com.danielflower.internalnpmserver;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private final int port;
    private final File npmCacheFolder;
    private final String npmRepositoryURL;
    private final String webServerHostName;

    public Config(int port, File npmCacheFolder, String npmRepositoryURL, String webServerHostName) {
        this.webServerHostName = webServerHostName;
        if (!npmRepositoryURL.startsWith("http://")) {
            throw new RuntimeException("The NPM repository '" + npmRepositoryURL
                    + "' is invalid; it must start with http://, for example: http://registry.npmjs.org/");
        }

        this.port = port;
        this.npmCacheFolder = npmCacheFolder;
        this.npmRepositoryURL = npmRepositoryURL;
    }

    public int getPort() {
        return port;
    }

    public File getNpmCacheFolder() {
        return npmCacheFolder;
    }

    public String getNpmRepositoryURL() {
        return npmRepositoryURL;
    }

    public static Config fromFile(String filePath) {
        File configFile = new File(filePath);
        if (!configFile.isFile()) {
            throw new RuntimeException("The file " + getCanonicalPath(configFile) + " does not exist");
        }

        Properties props = new Properties();
        try {
            FileReader reader = new FileReader(configFile);
            try {
                props.load(reader);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading config file " + filePath, e);
        }

        int port = Integer.parseInt(props.getProperty("port"));
        File cacheFolder = new File(props.getProperty("cacheFolder"));

        if (cacheFolder.mkdirs()) {
            log.info("Created " + getCanonicalPath(cacheFolder));
        }

        String npmURL = props.getProperty("npmRegistryURL");
        String webServerHostName = props.getProperty("webServerHostName");
        return new Config(port, cacheFolder, npmURL, webServerHostName);
    }

    private static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    public String getWebServerHostName() {
        return webServerHostName;
    }
}

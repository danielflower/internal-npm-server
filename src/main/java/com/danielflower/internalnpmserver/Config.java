package com.danielflower.internalnpmserver;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.Properties;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private final int port;
	private final File internalRepoFolder;
	private final File npmCacheFolder;
    private final String npmRepositoryURL;
    private final String webServerHostName;
    private final Proxy proxy;

    public Config(int port, File internalRepoFolder, File npmCacheFolder, String npmRepositoryURL, String webServerHostName, Proxy proxy) {
        this.webServerHostName = webServerHostName;
        this.proxy = proxy;
        if (!npmRepositoryURL.startsWith("http://")) {
            throw new RuntimeException("The NPM repository '" + npmRepositoryURL
                    + "' is invalid; it must start with http://, for example: http://registry.npmjs.org/");
        }

        this.port = port;
        this.npmCacheFolder = npmCacheFolder;
	    this.internalRepoFolder = internalRepoFolder;
        this.npmRepositoryURL = npmRepositoryURL;
    }

    public int getPort() {
        return port;
    }

	public File getNpmCacheFolder() {
		return npmCacheFolder;
	}

	public File getInternalRepoFolder() {
		return internalRepoFolder;
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
	    File cacheFolder = getCacheFolder(props);
	    File internalRepoFolder = getInternalRepoFolder(props);

	    String npmURL = props.getProperty("npmRegistryURL");
        String webServerHostName = props.getProperty("webServerHostName");
        Proxy proxy = getProxy(props);
        return new Config(port, internalRepoFolder, cacheFolder, npmURL, webServerHostName, proxy);
    }

	private static File getInternalRepoFolder(Properties props) {
		File internalRepoFolder = null;
		String repoFolderPath = props.getProperty("internalRepoFolder");
		if (repoFolderPath != null && repoFolderPath.length() > 0) {
		    internalRepoFolder = new File(repoFolderPath);
			if (internalRepoFolder.mkdirs()) {
				log.info("Created for internal repo: " + getCanonicalPath(internalRepoFolder));
			} else {
				log.info("Using the following folder to store the internally published artifacts: " + getCanonicalPath(internalRepoFolder));
			}
		}
		return internalRepoFolder;
	}

	private static File getCacheFolder(Properties props) {
		File cacheFolder = new File(props.getProperty("cacheFolder"));
		if (cacheFolder.mkdirs()) {
		    log.info("Created for NPM modules cache: " + getCanonicalPath(cacheFolder));
		} else {
		    log.info("Using the following folder to store NPM modules: " + getCanonicalPath(cacheFolder));
		}
		return cacheFolder;
	}

	private static Proxy getProxy(Properties props) {
        String proxyHost = props.getProperty("proxyHost");
        if (StringUtils.isBlank(proxyHost)) {
            return Proxy.NO_PROXY;
        }
        int proxyPort = Integer.parseInt(props.getProperty("proxyPort"));

        final String username = props.getProperty("proxyUsername");
        if (StringUtils.isNotBlank(username)) {
            final String password = props.getProperty("proxyPassword");
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
        }

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
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

    public Proxy getProxy() {
        return proxy;
    }

    public URL getNpmEndPoint() {
        try {
            return new URL("http", webServerHostName, port, "/npm/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); // yay for checked exceptions
        }
    }
}

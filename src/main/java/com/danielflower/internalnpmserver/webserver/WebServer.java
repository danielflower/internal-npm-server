package com.danielflower.internalnpmserver.webserver;

import com.danielflower.internalnpmserver.App;
import com.danielflower.internalnpmserver.Config;
import com.danielflower.internalnpmserver.controllers.HomepageHandler;
import com.danielflower.internalnpmserver.controllers.NpmHandler;
import com.danielflower.internalnpmserver.controllers.StaticHandlerImpl;
import com.danielflower.internalnpmserver.rendering.HttpViewRenderer;
import com.danielflower.internalnpmserver.rendering.NonCachableHttpViewRenderer;
import com.danielflower.internalnpmserver.rendering.VelocityViewRenderer;
import com.danielflower.internalnpmserver.rendering.ViewRenderer;
import com.danielflower.internalnpmserver.services.FileDownloader;
import com.danielflower.internalnpmserver.services.FileDownloaderImpl;
import com.danielflower.internalnpmserver.services.PackageReWritingFileDownloader;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static ViewRenderer viewRenderer = new VelocityViewRenderer("/views/");
    private static HttpViewRenderer httpViewRenderer = new NonCachableHttpViewRenderer(viewRenderer);
    public static final File STATIC_ROOT = new File("src/main/resources/webroot");

    private SocketConnection connection;
    private final Container webContainer;
    private final int port;
    private final String hostname;

    private WebServer(Container webContainer, int port, String hostname) {
        this.webContainer = webContainer;
        this.port = port;
        this.hostname = hostname;
    }

    public static WebServer createWebServer(Config config) {
        FileDownloader downloader = new PackageReWritingFileDownloader(new FileDownloaderImpl(), config.getNpmRepositoryURL(), "http://localhost:" + config.getPort() + "/npm");
        RequestHandler[] handlers = new RequestHandler[]{
                new HomepageHandler(httpViewRenderer),
                new NpmHandler(downloader, new StaticHandlerImpl(config.getNpmCacheFolder()), config.getNpmRepositoryURL(), config.getNpmCacheFolder()),
                new StaticHandlerImpl(STATIC_ROOT)
        };
        RequestRouter router = new RequestRouter(handlers);
        ErrorHandlingWebContainer errorHandler = new ErrorHandlingWebContainer(router);
        return new WebServer(new LoggingWebContainer(errorHandler), config.getPort(), config.getWebServerHostName());
    }

    public void start() throws IOException {
        this.connection = new SocketConnection(new ContainerServer(webContainer));
        InetSocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
        String localUrl = "http://" + hostname + ":" + address.getPort();
        log.info("Server started at " + localUrl);
        log.info("To use this as your NPM registry, run the following on your local PC:");
        log.info("npm config set registry " + localUrl + "/npm/");
    }

    public void stop() throws IOException {
        log.info("Stopping server...");
        connection.close();
        log.info("Server stopped.");
    }

}

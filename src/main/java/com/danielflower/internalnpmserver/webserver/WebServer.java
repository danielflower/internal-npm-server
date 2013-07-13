package com.danielflower.internalnpmserver.webserver;

import com.danielflower.internalnpmserver.App;
import com.danielflower.internalnpmserver.controllers.HomepageHandler;
import com.danielflower.internalnpmserver.controllers.NpmHandler;
import com.danielflower.internalnpmserver.controllers.StaticHandlerImpl;
import com.danielflower.internalnpmserver.rendering.HttpViewRenderer;
import com.danielflower.internalnpmserver.rendering.NonCachableHttpViewRenderer;
import com.danielflower.internalnpmserver.rendering.VelocityViewRenderer;
import com.danielflower.internalnpmserver.rendering.ViewRenderer;
import com.danielflower.internalnpmserver.services.FileDownloaderImpl;
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
    public static final File NPM_CACHE_FOLDER = new File("target/npmcache");
    static final RequestHandler[] DEFAULT_REQUEST_HANDLERS = new RequestHandler[]{
            new HomepageHandler(httpViewRenderer),
            new NpmHandler(new FileDownloaderImpl(), new StaticHandlerImpl(NPM_CACHE_FOLDER), "http://registry.npmjs.org/", NPM_CACHE_FOLDER),
            new StaticHandlerImpl(STATIC_ROOT)
    };

    private SocketConnection connection;
    private final Container webContainer;
    private final int port;

    public WebServer(Container webContainer, int port) throws IOException {
        this.webContainer = webContainer;
        this.port = port;
    }

    public static LoggingWebContainer createLoggingErrorHandlingRoutingContainer() {
        RequestRouter router = new RequestRouter(DEFAULT_REQUEST_HANDLERS);
        ErrorHandlingWebContainer errorHandler = new ErrorHandlingWebContainer(router);
        return new LoggingWebContainer(errorHandler);
    }

    public void start() throws IOException {
        this.connection = new SocketConnection(new ContainerServer(webContainer));
        InetSocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
        log.info("Server started at http://localhost:" + address.getPort());
    }

    public void stop() throws IOException {
        log.info("Stopping server...");
        connection.close();
        log.info("Server stopped.");
    }

}

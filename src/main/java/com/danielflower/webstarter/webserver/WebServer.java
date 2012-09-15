package com.danielflower.webstarter.webserver;

import com.danielflower.webstarter.App;
import com.danielflower.webstarter.controllers.HomepageHandler;
import com.danielflower.webstarter.controllers.StaticHandler;
import com.danielflower.webstarter.rendering.HttpViewRenderer;
import com.danielflower.webstarter.rendering.NonCachableHttpViewRenderer;
import com.danielflower.webstarter.rendering.VelocityViewRenderer;
import com.danielflower.webstarter.rendering.ViewRenderer;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static ViewRenderer viewRenderer = new VelocityViewRenderer("/views/");
    private static HttpViewRenderer httpViewRenderer = new NonCachableHttpViewRenderer(viewRenderer);
    static final RequestHandler[] DEFAULT_REQUEST_HANDLERS = new RequestHandler[]{
            new HomepageHandler(httpViewRenderer), new StaticHandler()
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
        this.connection = new SocketConnection(webContainer);
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

package com.danielflower.webstarter;

import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.LoggingWebContainer;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    static final RequestHandler[] DEFAULT_REQUEST_HANDLERS = new RequestHandler[]{
            new HomepageHandler(), new StaticHandler()
    };

    private SocketConnection connection;
    private final Container webContainer;
    private final int port;

    public App(Container webContainer, int port) throws IOException {
        this.webContainer = webContainer;
        this.port = port;
    }

    public static void main(String[] list) throws Exception {
        LoggingWebContainer logger = createLoggingErrorHandlingRoutingContainer();
        App app = new App(logger, 8081);
        app.start();
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

    public void stop() throws IOException, InterruptedException {
        log.info("Stopping server...");
        connection.close();
        log.info("Server stopped.");
    }


}

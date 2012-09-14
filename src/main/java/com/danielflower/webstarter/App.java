package com.danielflower.webstarter;

import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] list) throws Exception {
        RequestHandler[] handlers = new RequestHandler[]{
                new StaticHandler()
        };
        RequestRouter router = new RequestRouter(handlers);
        ErrorHandlingWebContainer errorHandler = new ErrorHandlingWebContainer(router);
        Connection connection = new SocketConnection(errorHandler);
        InetSocketAddress address = new InetSocketAddress(8081);
        connection.connect(address);
        log.info("Server started at http://localhost:" + address.getPort());
    }

}

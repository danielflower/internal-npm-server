package com.danielflower.internalnpmserver;

import com.danielflower.internalnpmserver.webserver.WebServer;
import com.danielflower.internalnpmserver.webserver.LoggingWebContainer;

import java.io.IOException;

public class App {

    public static void main(String[] list) throws Exception {
    	final int PORT = 8081;
    	
        LoggingWebContainer logger = WebServer.createLoggingErrorHandlingRoutingContainer();
        final WebServer app = new WebServer(logger, PORT);
        
        //In case when gradle does not print out
        System.out.println("About to start webserver on http://localhost:" + PORT);
        app.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    app.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

}

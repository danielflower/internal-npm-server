package com.danielflower.internalnpmserver;

import com.danielflower.internalnpmserver.webserver.WebServer;

import java.io.File;
import java.io.IOException;

public class App {

    public static void main(String[] list) throws Exception {
    	final int PORT = 8081;
        final WebServer app = WebServer.createWebServer(PORT, new File("target/npmcache"), "http://registry.npmjs.org/");

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

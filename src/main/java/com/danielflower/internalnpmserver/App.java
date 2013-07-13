package com.danielflower.internalnpmserver;

import com.danielflower.internalnpmserver.webserver.WebServer;

import java.io.IOException;

public class App {

    public static void main(String[] list) throws Exception {
        if (list.length != 1) {
            System.out.println("Start with an argument pointing to a config file");
            return;
        }
        Config config = Config.fromFile(list[0]);
        final WebServer app = WebServer.createWebServer(config);

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

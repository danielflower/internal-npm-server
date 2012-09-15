package com.danielflower.webstarter;

import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AppTest {

    private final int port = 54321;

    @Test
    public void serverCanBeStartedAndStoppedMultipleTimes() throws Exception {
        App app = new App(App.createLoggingErrorHandlingRoutingContainer(), port);

        assertRequestsCannotBeMade();

        app.start();
        assertThat(getResponseCodeOfHomepage(), is(200));

        app.stop();
        assertRequestsCannotBeMade();

        app.start();
        assertThat(getResponseCodeOfHomepage(), is(200));

        app.stop();
    }

    private void assertRequestsCannotBeMade() throws IOException {
        try {
            getResponseCodeOfHomepage();
            fail("Should not have been able to make a request");
        } catch (ConnectException ce) {
            // expected
        }
    }

    private int getResponseCodeOfHomepage() throws IOException {
        URL serverURL = new URL("http://127.0.0.1:" + port);
        HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setReadTimeout(10000);
        connection.connect();
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        return responseCode;
    }
}

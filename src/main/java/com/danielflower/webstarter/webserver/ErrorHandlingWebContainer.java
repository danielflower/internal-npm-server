package com.danielflower.webstarter.webserver;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;

public class ErrorHandlingWebContainer implements Container {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingWebContainer.class);
    private final Container underlying;

    public ErrorHandlingWebContainer(Container underlying) {
        this.underlying = underlying;
    }

    @Override
    public void handle(Request req, Response resp) {
        try {
            try {
                underlying.handle(req, resp);
            } catch (ResourceNotFoundException rnfe) {
                log.warn("404 error for " + rnfe.getRequestedTarget());
                writeErrorToClient(resp, 404, "404 Not Found", "Sorry, could not find " + rnfe.getRequestedTarget());
            }
        } catch (Exception e) {
            Throwable cause = (e.getCause() == null) ? e : e.getCause();
            log.error("Unhandled exception for " + req.getTarget(), cause);
            String message = "Oops, an error occurred, sorry about that.  Technical details for the geeks: " + cause;
            writeErrorToClient(resp, 500, "500 Internal Error", message);
        }
    }

    private void writeErrorToClient(Response resp, int httpErrorCode, String httpErrorMessage, String messageDisplayedToUser) {
        try {
            resp.setCode(httpErrorCode);
            resp.setText(httpErrorMessage);
            PrintStream printStream = resp.getPrintStream();
            printStream.print(messageDisplayedToUser);
            printStream.close();
            resp.close();
        } catch (IOException e) {
            log.info("Error while writing error to client: " + e.getMessage());
        }
    }
}

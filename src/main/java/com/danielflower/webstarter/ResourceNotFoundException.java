package com.danielflower.webstarter;

public class ResourceNotFoundException extends RuntimeException {

    private final String requestedTarget;

    public ResourceNotFoundException(String requestedTarget) {
        super("No resource found at " + requestedTarget);
        this.requestedTarget = requestedTarget;
    }

    public String getRequestedTarget() {
        return requestedTarget;
    }
}

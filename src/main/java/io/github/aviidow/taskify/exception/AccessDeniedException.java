package io.github.aviidow.taskify.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String resourceName, Long resourceId, String action) {
        super(String.format("You don't have permission to %s %s with id: %d", action, resourceName, resourceId));
    }
}

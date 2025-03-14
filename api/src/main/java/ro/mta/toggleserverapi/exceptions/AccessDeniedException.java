package ro.mta.toggleserverapi.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super("The user is not allowed to access this resource: " + message);
    }
}

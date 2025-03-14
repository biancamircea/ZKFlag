package ro.mta.toggleserverapi.exceptions;

public class ResourceNotValidException extends RuntimeException {
    private final String message;

    public ResourceNotValidException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

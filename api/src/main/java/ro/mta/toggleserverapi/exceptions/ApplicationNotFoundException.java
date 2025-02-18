package ro.mta.toggleserverapi.exceptions;

public class ApplicationNotFoundException extends ResourceNotFoundException {
    public ApplicationNotFoundException(Long appId) {
        super("Could not find application with id " + appId);
    }
}

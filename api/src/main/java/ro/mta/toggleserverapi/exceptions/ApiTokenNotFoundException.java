package ro.mta.toggleserverapi.exceptions;

public class ApiTokenNotFoundException extends ResourceNotFoundException {
    public ApiTokenNotFoundException(Long id) {
        super("Could not find ApiToken with id " + id);
    }
}

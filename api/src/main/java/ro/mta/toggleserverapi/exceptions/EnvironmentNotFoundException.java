package ro.mta.toggleserverapi.exceptions;

public class EnvironmentNotFoundException extends ResourceNotFoundException{
    public EnvironmentNotFoundException(Long id) {
        super("Could not find environment with id " + id);
    }
}

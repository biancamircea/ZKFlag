package ro.mta.toggleserverapi.exceptions;

public class ProjectNotFoundException extends ResourceNotFoundException{
    public ProjectNotFoundException(Long id) {
        super("Could not find project with id " + id);
    }
}

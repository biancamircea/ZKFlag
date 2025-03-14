package ro.mta.toggleserverapi.exceptions;

public class ProjectRoleNotFoundException extends ResourceNotFoundException {
    public ProjectRoleNotFoundException(Long projectRoleId) {
        super("Could not find project role with id " + projectRoleId);
    }
}

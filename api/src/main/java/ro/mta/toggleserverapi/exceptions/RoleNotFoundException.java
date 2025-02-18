package ro.mta.toggleserverapi.exceptions;

public class RoleNotFoundException extends ResourceNotFoundException{

    public RoleNotFoundException(Long id) {
        super("Could not find role with id " + id);
    }
}

package ro.mta.toggleserverapi.exceptions;

public class RoleNotFoundException extends ResourceNotFoundException{

    public RoleNotFoundException(String name) {
        super("Could not find role with name " + name);
    }
}

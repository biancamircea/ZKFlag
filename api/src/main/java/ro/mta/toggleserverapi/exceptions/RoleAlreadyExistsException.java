package ro.mta.toggleserverapi.exceptions;

import ro.mta.toggleserverapi.enums.UserRoleType;

public class RoleAlreadyExistsException extends ResourceAlreadyExistsException{
    public RoleAlreadyExistsException(UserRoleType type, String link) {
        super("Role " + type + " already exists", link);
    }
}

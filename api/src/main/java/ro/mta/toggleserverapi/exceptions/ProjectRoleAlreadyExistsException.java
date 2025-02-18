package ro.mta.toggleserverapi.exceptions;

import ro.mta.toggleserverapi.enums.ProjectRoleType;

public class ProjectRoleAlreadyExistsException extends ResourceAlreadyExistsException {
    public ProjectRoleAlreadyExistsException(ProjectRoleType type, String link) {
        super("Role " + type + " already exists", link);
    }
}

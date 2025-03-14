package ro.mta.toggleserverapi.exceptions;

public class ContextFieldNotFoundException extends ResourceNotFoundException {
    public ContextFieldNotFoundException(Long contextId, Long projectId) {
        super("Could not find context field with id " + contextId + " in project " + projectId);
    }

    public ContextFieldNotFoundException(String name, Long projectId){
        super("Could not find context field with name " + name  + " in project " + projectId);
    }
}

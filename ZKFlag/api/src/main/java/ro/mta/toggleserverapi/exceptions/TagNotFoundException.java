package ro.mta.toggleserverapi.exceptions;

public class TagNotFoundException extends ResourceNotFoundException {
    public TagNotFoundException(Long tagId, Long projectId) {
        super("Could not find tag with id " + tagId + " in project " + projectId);
    }
}

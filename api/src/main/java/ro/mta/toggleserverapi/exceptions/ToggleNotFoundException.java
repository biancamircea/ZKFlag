package ro.mta.toggleserverapi.exceptions;

public class ToggleNotFoundException extends ResourceNotFoundException{
    public ToggleNotFoundException(Long id) {
        super("Could not find toggle " + id);
    }

    public ToggleNotFoundException(Long toggleId, Long projectId){
        super("Could not find toggle " + toggleId +" in project " + projectId);
    }
    public ToggleNotFoundException(String toggleName, Long projectId){
        super("Could not find toggle " + toggleName +" in project " + projectId);
    }
}

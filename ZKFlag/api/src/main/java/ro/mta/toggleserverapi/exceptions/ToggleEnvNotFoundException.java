package ro.mta.toggleserverapi.exceptions;

public class ToggleEnvNotFoundException extends ResourceNotFoundException{
    public ToggleEnvNotFoundException(Long toggleId, Long envId, Long instanceId) {
        super("Could not find toggle "+toggleId+" in environment "+ envId+" for instance "+instanceId);
    }

    public ToggleEnvNotFoundException(Long toggleId, String envName,Long instanceId) {
        super("Could not find toggle "+toggleId+" in environment "+ envName+" for instance "+instanceId);
    }
}

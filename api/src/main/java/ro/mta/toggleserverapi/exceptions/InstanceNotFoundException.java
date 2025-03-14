package ro.mta.toggleserverapi.exceptions;

public class InstanceNotFoundException extends ResourceNotFoundException{
        public InstanceNotFoundException(Long id) {
            super("Could not find instance with id " + id);
    }
}

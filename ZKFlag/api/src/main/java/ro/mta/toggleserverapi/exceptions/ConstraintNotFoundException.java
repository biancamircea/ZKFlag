package ro.mta.toggleserverapi.exceptions;

public class ConstraintNotFoundException extends ResourceNotFoundException {

    public ConstraintNotFoundException(Long constraintId) {
        super("Could not find constraint with id " + constraintId );
    }
}

package ro.mta.toggleserverapi.exceptions;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException(Long id) {
        super("Could not find user with id " + id);
    }
    public UserNotFoundException(Long userId, Long projectId) {
        super("Could not find user with id " + userId + " in project with id "+projectId);
    }
}

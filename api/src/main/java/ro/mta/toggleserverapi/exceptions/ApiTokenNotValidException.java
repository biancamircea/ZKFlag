package ro.mta.toggleserverapi.exceptions;

public class ApiTokenNotValidException extends ResourceNotValidException {
    public ApiTokenNotValidException(){
        super("Token not valid.");
    }
}

package ro.mta.toggleserverapi.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ro.mta.toggleserverapi.exceptions.ResourceNotValidException;

@ControllerAdvice
public class ResourceNotValidAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ResourceNotValidException.class)
    String resourceNotValidHandler(ResourceNotValidException exception){
        return exception.getMessage();
    }
}

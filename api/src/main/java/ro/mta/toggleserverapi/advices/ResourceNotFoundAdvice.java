package ro.mta.toggleserverapi.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ro.mta.toggleserverapi.exceptions.ProjectNotFoundException;
import ro.mta.toggleserverapi.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ResourceNotFoundAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    String resourceNotFoundHandler(ResourceNotFoundException exception){
        return exception.getMessage();
    }
}

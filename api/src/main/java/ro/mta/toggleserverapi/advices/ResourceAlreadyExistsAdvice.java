package ro.mta.toggleserverapi.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ro.mta.toggleserverapi.exceptions.ResourceAlreadyExistsException;

import java.util.Map;

@ControllerAdvice
public class ResourceAlreadyExistsAdvice {
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> resourceAlreadyExistsHandler(ResourceAlreadyExistsException exception){
        String message = exception.getMessage();
        String link = exception.getLink();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", message, "link", link));
    }
}

package sae.semestre.six.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<String> handlerInvalidDataException(InvalidDataException ex) {
        // TODO Message plus adapté à un mode débug ou dev
        return ResponseEntity.badRequest().body(ex.getMessage() + "\n" + ex.getStackTrace()[0] + "\n" + ex.getClass());
    }
}

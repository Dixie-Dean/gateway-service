package dev.dixie.advice;

import dev.dixie.exception.EmailAlreadyTakenException;
import dev.dixie.exception.ImagerPostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GatewayControllerAdvice {

    @ExceptionHandler(ImagerPostNotFoundException.class)
    public ResponseEntity<String> handleImagerPostNotFoundException(ImagerPostNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<String> handleEmailAlreadyTakenException(EmailAlreadyTakenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

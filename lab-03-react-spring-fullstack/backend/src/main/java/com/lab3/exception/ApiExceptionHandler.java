package com.lab3.exception;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(NotFoundException.class) ResponseEntity<Void> notFound(){return ResponseEntity.notFound().build();}
 @ExceptionHandler(InvalidTransitionException.class) ResponseEntity<Void> conflict(){return ResponseEntity.status(HttpStatus.CONFLICT).build();}
}

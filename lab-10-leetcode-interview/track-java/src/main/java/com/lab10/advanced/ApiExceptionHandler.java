package com.lab10.advanced;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TransferNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(TransferNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(InvalidTransferStateException.class)
    ResponseEntity<Map<String, String>> invalidState(InvalidTransferStateException exception) {
        return ResponseEntity.unprocessableEntity().body(Map.of("error", exception.getMessage()));
    }
}

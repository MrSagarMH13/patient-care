package com.makeen.patientcare.exception;

import com.makeen.patientcare.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ResponseDTO<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    //global exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Object>> globalExceptionHandler(Exception ex) {
        return new ResponseEntity<>(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

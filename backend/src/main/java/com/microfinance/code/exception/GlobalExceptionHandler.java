package com.microfinance.code.exception;

import com.microfinance.code.etc.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(NotFoundException ex){
        ApiResponse<?> errorResponse = ApiResponse.error(HttpStatus.NOT_FOUND,HttpStatus.NOT_FOUND.value(),ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyException.class)
    public ResponseEntity<ApiResponse<?>> handleEmptyException(EmptyException ex){
        ApiResponse<?> errorResponse = ApiResponse.error(HttpStatus.NOT_FOUND,HttpStatus.NOT_FOUND.value(),ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }
}

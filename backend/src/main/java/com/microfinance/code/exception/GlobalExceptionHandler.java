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
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ApiResponse<String>> handleAlreadyExistException(AlreadyExistException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)  // 409 Conflict
                .body(ApiResponse.error(HttpStatus.CONFLICT, 409, ex.getMessage()));
    }


    @ExceptionHandler(EmptyException.class)
    public ResponseEntity<ApiResponse<?>> handleEmptyException(EmptyException ex){
        ApiResponse<?> errorResponse = ApiResponse.error(HttpStatus.NOT_FOUND,HttpStatus.NOT_FOUND.value(),ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500 Internal Server Error
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "An unexpected error occurred: " + ex.getMessage()));
    }
}

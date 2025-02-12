package com.microfinance.code.etc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class ApiResponse<T> {
    private HttpStatus httpStatus;
    private int statusCode;
    private String message;
    private String status;
    private T data;
    private String token;
    private ApiResponse(HttpStatus httpStatus,int statusCode, String message) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.message = message;

    }
    //    private ApiResponse(HttpStatus httpStatus,int statusCode, String message,String status) {
//        this.httpStatus = httpStatus;
//        this.statusCode = statusCode;
//        this.message = message;
//        this.status = status;
//
//    }
    private ApiResponse(HttpStatus httpStatus,int statusCode, String message, T data) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
    private ApiResponse(HttpStatus httpStatus,int statusCode, String message, String token) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.message = message;
        this.token = token;
    }
    private ApiResponse(HttpStatus httpStatus,int statusCode, String message, T data,String status) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public ApiResponse() {

    }

    // Static method for success responses
    public static <T> ApiResponse<T> success(HttpStatus httpStatus,int statusCode, String message, T data) {
        return new ApiResponse<>(httpStatus,statusCode, message, data);
    }
    public static <T> ApiResponse<T> success(HttpStatus httpStatus,int statusCode, String message, String token) {
        return new ApiResponse<>(httpStatus,statusCode, message, token);
    }
    public static <T> ApiResponse<T> success(HttpStatus httpStatus,int statusCode, String message, T data,String status) {
        return new ApiResponse<>(httpStatus,statusCode, message, data,status);
    }
    public static <T> ApiResponse<T> success(HttpStatus httpStatus,int statusCode, String message) {
        return new ApiResponse<>(httpStatus,statusCode, message);
    }
//    public static <T> ApiResponse<T> success(HttpStatus httpStatus,int statusCode, String message,String status) {
//        return new ApiResponse<>(httpStatus,statusCode, message,status);
//    }

    // Static method for error responses (without data)
    public static <T> ApiResponse<T> error(HttpStatus httpStatus,int statusCode, String message) {
        return new ApiResponse<>(httpStatus,statusCode, message, null);
    }
    public static <T> ApiResponse<T> error(HttpStatus httpStatus,int statusCode, String message,String status) {
        return new ApiResponse<>(httpStatus,statusCode, message, null,status);
    }

    // Static method for error responses (with data)
    public static <T> ApiResponse<T> error(HttpStatus httpStatus,int statusCode, String message, T data) {
        return new ApiResponse<>(httpStatus,statusCode, message, data);
    }
    public static <T> ApiResponse<T> error(HttpStatus httpStatus,int statusCode, String message, T data,String status) {
        return new ApiResponse<>(httpStatus,statusCode, message, data,status);
    }


}

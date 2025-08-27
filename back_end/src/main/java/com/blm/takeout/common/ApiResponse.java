package com.blm.takeout.common;

import org.springframework.http.HttpStatus;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp = System.currentTimeMillis();

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static ApiResponse<?> error(HttpStatus status, String message) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(status.value());
        response.setMessage(message);
        return response;
    }

    public static ApiResponse<?> error(int code, String message) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
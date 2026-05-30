package com.mgps.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard API Response wrapper for all endpoints.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;
    private LocalDateTime timestamp;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data, ErrorDetails error, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public ErrorDetails getError() { return error; }
    public void setError(ErrorDetails error) { this.error = error; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, message, null, ErrorDetails.builder()
            .code(errorCode)
            .message(message)
            .build(), LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String errorCode, String message, String details) {
        return new ApiResponse<>(false, message, null, ErrorDetails.builder()
            .code(errorCode)
            .message(message)
            .details(details)
            .build(), LocalDateTime.now());
    }

    public static final class ErrorDetails {
        private String code;
        private String message;
        private String details;

        public ErrorDetails() {
        }

        public ErrorDetails(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String code;
            private String message;
            private String details;

            private Builder() {
            }

            public Builder code(String code) { this.code = code; return this; }
            public Builder message(String message) { this.message = message; return this; }
            public Builder details(String details) { this.details = details; return this; }

            public ErrorDetails build() {
                return new ErrorDetails(code, message, details);
            }
        }
    }
}

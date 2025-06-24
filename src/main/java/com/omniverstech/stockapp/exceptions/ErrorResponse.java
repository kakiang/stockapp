package com.omniverstech.stockapp.exceptions;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class ErrorResponse {
    private int status;
    private String message;
    private Map<String, String> details;
    private Instant timestamp;

    public ErrorResponse(int status, String message) {
        this(status, message, null);
    }

    public ErrorResponse(int status, String message, Map<String, String> details) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = Instant.now();
    }


}

package com.github.spector517.veeapp.backend.exception;

import lombok.Getter;

@Getter
public class XrayCtlExecutionException extends RuntimeException {
    private final String stderr;

    public XrayCtlExecutionException(String message, String stderr) {
        super(message);
        this.stderr = stderr;
    }

    public XrayCtlExecutionException(String message, String stderr, Throwable cause) {
        super(message, cause);
        this.stderr = stderr;
    }
}
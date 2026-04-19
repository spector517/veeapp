package com.github.spector517.veeapp.backend.exception;

import com.github.spector517.veeapp.backend.command.CommandExecutionException;
import com.github.spector517.veeapp.backend.dto.shared.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<ErrorResponse> handleCommandExecution(
            CommandExecutionException ex,
            HttpServletRequest request
    ) {
        log.error("Command execution error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(XrayCtlExecutionException.class)
    public ResponseEntity<ErrorResponse> handleXrayCtl(
            XrayCtlExecutionException ex,
            HttpServletRequest request
    ) {
        var message = ex.getMessage();
        if (ex.getStderr() != null && !ex.getStderr().isBlank()) {
            message += ": " + ex.getStderr();
        }
        log.error("XrayCtl execution error: {}", message);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        var message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}
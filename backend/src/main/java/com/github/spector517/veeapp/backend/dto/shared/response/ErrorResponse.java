package com.github.spector517.veeapp.backend.dto.shared.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String path
) { }
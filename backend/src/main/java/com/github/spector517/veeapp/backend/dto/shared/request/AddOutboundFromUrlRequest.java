package com.github.spector517.veeapp.backend.dto.shared.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public record AddOutboundFromUrlRequest(
        @NotBlank String url,
        Optional<String> name
) {}
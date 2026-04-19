package com.github.spector517.veeapp.backend.dto.shared.request;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

public record XrayConfigRequest(
        @NotBlank String host,
        Optional<Integer> port,
        Optional<String> realityHost,
        Optional<Integer> realityPort,
        List<String> realityNames,
        Optional<String> name,
        Optional<Boolean> clean
) {}

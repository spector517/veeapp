package com.github.spector517.veeapp.backend.dto.shared.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ClientNamesRequest(
        @NotEmpty List<String> names
) {}
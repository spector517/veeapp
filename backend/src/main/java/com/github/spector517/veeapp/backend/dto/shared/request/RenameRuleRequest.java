package com.github.spector517.veeapp.backend.dto.shared.request;

import jakarta.validation.constraints.NotBlank;

public record RenameRuleRequest(
        @NotBlank String newName
) {}
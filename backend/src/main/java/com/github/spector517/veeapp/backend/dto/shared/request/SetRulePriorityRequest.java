package com.github.spector517.veeapp.backend.dto.shared.request;

import jakarta.validation.constraints.NotNull;

public record SetRulePriorityRequest(
        @NotNull Integer priority
) {}
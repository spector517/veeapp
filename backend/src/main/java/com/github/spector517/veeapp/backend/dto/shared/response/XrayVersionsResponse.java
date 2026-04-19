package com.github.spector517.veeapp.backend.dto.shared.response;

import java.util.List;

public record XrayVersionsResponse(
        List<String> releases
) {}
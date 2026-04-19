package com.github.spector517.veeapp.backend.dto.shared.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public record AddOutboundRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String uuid,
        @NotBlank String sni,
        @NotBlank String shortId,
        @NotBlank String password,
        @NotBlank String spiderX,
        @NotNull Integer port,
        @NotNull Fingerprint fingerprint
) {
        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @Getter
        public enum Fingerprint {
                CHROME("chrome"),
                FIREFOX("firefox"),
                SAFARI("safari"),
                IOS("ios"),
                ANDROID("android"),
                EDGE("edge"),
                QQ("qq"),
                RANDOM("random"),
                RANDOMIZED("randomized");

                private final String value;
        }
}
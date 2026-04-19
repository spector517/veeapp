package com.github.spector517.veeapp.backend.dto.shared.request;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public record ChangeOutboundRequest(
        Optional<String> address,
        Optional<String> uuid,
        Optional<String> sni,
        Optional<String> password,
        Optional<String> shortId,
        Optional<String> spiderX,
        Optional<Integer> port,
        Optional<Fingerprint> fingerprint,
        Optional<String> newName
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
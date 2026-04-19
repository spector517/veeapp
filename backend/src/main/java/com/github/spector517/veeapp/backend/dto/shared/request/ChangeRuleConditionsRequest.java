package com.github.spector517.veeapp.backend.dto.shared.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;

public record ChangeRuleConditionsRequest(
        @NotNull Action action,
        Optional<String> domain,
        Optional<String> ip,
        Optional<String> port,
        Optional<Protocol> protocol
) {
        @RequiredArgsConstructor
        public enum Action {
                PUT("put"),
                DEL("del");

                public final String value;
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @Getter
        public enum Protocol {
                HTTP("http"),
                TLS("tls"),
                QUIC("quic"),
                BITTORRENT("bittorrent");

                public final String value;
        }
}
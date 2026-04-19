package com.github.spector517.veeapp.backend.dto.shared.request;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

public record AddRoutingRuleRequest(
        @NotBlank String name,
        @NotBlank String outbound,
        Optional<List<String>> domain,
        Optional<List<String>> ip,
        Optional<List<String>> port,
        Optional<Protocol> protocol,
        Optional<Integer> priority
) {
        @RequiredArgsConstructor
        public enum Protocol {
                HTTP("http"),
                TLS("tls"),
                QUIC("quic"),
                BITTORRENT("bittorrent");

                public final String value;
        }
}
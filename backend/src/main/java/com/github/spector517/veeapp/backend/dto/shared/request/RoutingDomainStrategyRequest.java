package com.github.spector517.veeapp.backend.dto.shared.request;

import lombok.RequiredArgsConstructor;

public record RoutingDomainStrategyRequest(
        DomainStrategy strategy
) {
        @RequiredArgsConstructor
        public enum DomainStrategy {
                AS_IS("AsIs"),
                IP_IF_NOT_MATCH("IPIfNonMatch"),
                IP_ON_DEMAND("IPOnDemand");

                public final String value;
        }
}
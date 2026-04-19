package com.github.spector517.veeapp.backend.dto.shared.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record StatusResponse(
        @JsonProperty("veepeenet_version") String veepeenetVersion,
        @JsonProperty("veepeenet_build") Integer veepeenetBuild,
        @JsonProperty("xray_version") String xrayVersion,
        @JsonProperty("server_status") String serverStatus,
        Boolean enabled,
        String uptime,
        @JsonProperty("restart_required") Boolean restartRequired,
        @JsonProperty("server_host") String serverHost,
        @JsonProperty("server_port") String serverPort,
        @JsonProperty("reality_address") String realityAddress,
        @JsonProperty("reality_names") List<String> realityNames,
        @JsonProperty("server_name") String serverName,
        Clients clients,
        Routing routing,
        List<Outbound> outbounds
) {
        public record Clients(
                List<Client> clients
        ) {
                public record Client(
                        String name,
                        String url
                ) {}
        }

        public record Routing(
                @JsonProperty("domain_strategy") String domainStrategy,
                List<RoutingRule> rules
        ) {
                public record RoutingRule(
                        String name,
                        List<String> domains,
                        List<String> ips,
                        List<String> ports,
                        List<String> protocols,
                        @JsonProperty("outbound_name") String outboundName,
                        Integer priority
                ) {}
        }

        public record Outbound(
                String name,
                String address,
                String uuid,
                String sni,
                @JsonProperty("short_id") String shortId,
                String password,
                @JsonProperty("spider_x") String spiderX,
                Integer port,
                String fingerprint,
                @JsonProperty("interface") String iface
        ) {}
}
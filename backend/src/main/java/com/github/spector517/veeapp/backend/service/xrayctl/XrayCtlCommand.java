package com.github.spector517.veeapp.backend.service.xrayctl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.spector517.veeapp.backend.command.Command;

public class XrayCtlCommand extends Command {

    public XrayCtlCommand() {
        super("xrayctl");
        sudo(true);
    }

    public XrayCtlCommand config(
        String host,
        int port,
        String realityHost,
        int realityPort,
        List<String> realityNames,
        String name,
        boolean clean
    ) {
        subcommands(List.of("config"));
        if (host != null && !host.isBlank()) {
            options().add(Map.entry("--host", Optional.of(host)));
        }
        if (port > 0) {
            options().add(Map.entry("--port", Optional.of(String.valueOf(port))));
        }
        if (realityHost != null && !realityHost.isBlank()) {
            options().add(Map.entry("--reality-host", Optional.of(realityHost)));
        }
        if (realityPort > 0) {
            options().add(Map.entry("--reality-port", Optional.of(String.valueOf(realityPort))));
        }
        if (realityNames != null) {
            realityNames.stream()
                .filter(s -> s != null && !s.isBlank())
                .forEach(v -> options().add(Map.entry("--reality-names", Optional.of(v))));
        }
        if (name != null && !name.isBlank()) {
            options().add(Map.entry("--name", Optional.of(name)));
        }
        if (clean) {
            options().add(Map.entry("--clean", Optional.empty()));
        }
        return this;
    }

    public XrayCtlCommand status() {
        subcommands(List.of("status"));
        options().add(Map.entry("--json", Optional.empty()));
        return this;
    }

    public XrayCtlCommand start() {
        subcommands(List.of("start"));
        return this;
    }

    public XrayCtlCommand stop() {
        subcommands(List.of("stop"));
        return this;
    }

    public XrayCtlCommand restart() {
        subcommands(List.of("restart"));
        return this;
    }

    public XrayCtlCommand updateGeodata() {
        subcommands(List.of("update-geodata"));
        return this;
    }

    public XrayCtlCommand addClients(List<String> clients) {
        subcommands(List.of("clients", "add"));
        clients.stream()
            .filter(s -> s != null && !s.isBlank())
            .forEach(c -> args().add(c));
        return this;
    }

    public XrayCtlCommand removeClients(List<String> clients) {
        subcommands(List.of("clients", "remove"));
        clients.stream()
            .filter(s -> s != null && !s.isBlank())
            .forEach(c -> args().add(c));
        return this;
    }

    public XrayCtlCommand addOutbound(
        String address,
        String uuid,
        String sni,
        String shortId,
        String password,
        String spiderX,
        Integer port,
        String fingerprint
    ) {
        options().add(Map.entry("--address", Optional.of(address)));
        options().add(Map.entry("--uuid", Optional.of(uuid)));
        options().add(Map.entry("--sni", Optional.of(sni)));
        options().add(Map.entry("--short-id", Optional.of(shortId)));
        options().add(Map.entry("--spider-x", Optional.of(spiderX)));
        options().add(Map.entry("--password", Optional.of(password)));
        options().add(Map.entry("--port", Optional.of(String.valueOf(port))));
        options().add(Map.entry("--fingerprint", Optional.of(fingerprint)));
        return this;
    }

    public XrayCtlCommand addOutboundFromUrl(String url, String name) {
        subcommands(List.of("outbounds", "add-from-url"));
        args().add(url);
        if (name != null && !name.isBlank()) {
            options().add(Map.entry("--name", Optional.of(name)));
        }
        return this;
    }

    public XrayCtlCommand changeOutbound(
        String name,
        String address,
        String uuid,
        String sni,
        String shortId,
        String password,
        String spiderX,
        Integer port,
        String fingerprint,
        String newName
    ) {
        subcommands(List.of("outbounds", "change"));
        args().add(name);
        if (address != null && !address.isBlank()) {
            options().add(Map.entry("--address", Optional.of(address)));
        }
        if (uuid != null && !uuid.isBlank()) {
            options().add(Map.entry("--uuid", Optional.of(uuid)));
        }
        if (sni != null && !sni.isBlank()) {
            options().add(Map.entry("--sni", Optional.of(sni)));
        }
        if (shortId != null && !shortId.isBlank()) {
            options().add(Map.entry("--short-id", Optional.of(shortId)));
        }
        if (password != null && !password.isBlank()) {
            options().add(Map.entry("--password", Optional.of(password)));
        }
        if (spiderX != null && !spiderX.isBlank()) {
            options().add(Map.entry("--spider-x", Optional.of(spiderX)));
        }
        if (port != null && port > 0) {
            options().add(Map.entry("--port", Optional.of(String.valueOf(port))));
        }
        if (fingerprint != null && !fingerprint.isBlank()) {
            options().add(Map.entry("--fingerprint", Optional.of(fingerprint)));
        }
        if (newName != null && !newName.isBlank()) {
            options().add(Map.entry("--new-name", Optional.of(newName)));
        }
        return this;
    }

    public XrayCtlCommand removeOutbound(String name) {
        subcommands(List.of("outbounds", "remove"));
        args().add(name);
        return this;
    }

    public XrayCtlCommand addRoutingRule(
        String name,
        String outbound,
        List<String> domain,
        List<String> ip,
        List<String> ports,
        String protocol,
        Integer priority
    ) {
        subcommands(List.of("routing", "add-rule"));
        args().add(name);
        options().add(Map.entry("--outbound", Optional.of(outbound)));
        if (domain != null) {
            domain.stream()
                .filter(s -> s != null && !s.isBlank())
                .forEach(v -> options().add(Map.entry("--domain", Optional.of(v))));
        }
        if (ip != null) {
            ip.stream()
                .filter(s -> s != null && !s.isBlank())
                .forEach(v -> options().add(Map.entry("--ip", Optional.of(v))));
        }
        if (ports != null) {
            ports.stream()
                .filter(s -> s != null && !s.isBlank())
                .forEach(v -> options().add(Map.entry("--ports", Optional.of(v))));
        }
        if (protocol != null && !protocol.isBlank()) {
            options().add(Map.entry("--protocol", Optional.of(protocol)));
        }
        if (priority != null) {
            options().add(Map.entry("--priority", Optional.of(String.valueOf(priority))));
        }
        return this;
    }

    public XrayCtlCommand removeRoutingRule(String name) {
        subcommands(List.of("routing", "remove-rule"));
        args().add(name);
        return this;
    }

    public XrayCtlCommand renameRoutingRule(String name, String newName) {
        subcommands(List.of("routing", "rename-rule"));
        args().add(name);
        options().add(Map.entry("--new-name", Optional.of(newName)));
        return this;
    }

    public XrayCtlCommand setRulePriority(String name, int priority) {
        subcommands(List.of("routing", "set-priority"));
        args().add(name);
        options().add(Map.entry("--priority", Optional.of(String.valueOf(priority))));
        return this;
    }

    public XrayCtlCommand changeRuleConditions(
        String name,
        String action,
        String domain,
        String ip,
        String ports,
        String protocol
    ) {
        subcommands(List.of("routing", "change-rule"));
        args().add(name);
        args().add(action);
        if (domain != null && !domain.isBlank()) {
            options().add(Map.entry("--domain", Optional.of(domain)));
        }
        if (ip != null && !ip.isBlank()) {
            options().add(Map.entry("--ip", Optional.of(ip)));
        }
        if (ports != null && !ports.isBlank()) {
            options().add(Map.entry("--ports", Optional.of(ports)));
        }
        if (protocol != null && !protocol.isBlank()) {
            options().add(Map.entry("--protocol", Optional.of(protocol)));
        }
        return this;
    }

    public XrayCtlCommand setDomainStrategy(String strategy) {
        subcommands(List.of("routing", "set-domain-strategy"));
        args().add(strategy);
        return this;
    }

    public XrayCtlCommand getXrayVersions(int limit) {
        subcommands(List.of("update-xray"));
        options().add(Map.entry("--list", Optional.empty()));
        options().add(Map.entry("--limit", Optional.of(String.valueOf(limit))));
        options().add(Map.entry("--json", Optional.empty()));
        return this;
    }

    public XrayCtlCommand updateXray(String version) {
        subcommands(List.of("update-xray"));
        options().add(Map.entry("--version", Optional.of(version)));
        return this;
    }
}
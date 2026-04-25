package com.github.spector517.veeapp.backend.service.xrayctl;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.github.spector517.veeapp.backend.command.Command;
import com.github.spector517.veeapp.backend.command.CommandExecutor;
import com.github.spector517.veeapp.backend.dto.shared.request.*;
import com.github.spector517.veeapp.backend.dto.shared.response.StatusResponse;
import com.github.spector517.veeapp.backend.dto.shared.response.XrayVersionsResponse;
import com.github.spector517.veeapp.backend.exception.XrayCtlExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class XrayCtlService {

    private final CommandExecutor commandExecutor;
    private final ObjectMapper objectMapper;

    @Value("${app.xray.config-path}")
    private String xrayConfigPath;

    @Value("${app.server.init-conf-path}")
    private String initConfPath;

    public boolean isConfigured() {
        return Files.exists(Path.of(xrayConfigPath));
    }

    public String getServerAddress() {
        var path = Path.of(initConfPath);
        if (!Files.exists(path)) {
            return "";
        }
        try {
            return Files.readAllLines(path).stream()
                    .filter(line -> line.startsWith("IP_ADDRESS="))
                    .map(line -> line.substring("IP_ADDRESS=".length()).strip())
                    .findFirst()
                    .orElse("");
        } catch (IOException e) {
            log.warn("Failed to read init conf: {}", e.getMessage());
            return "";
        }
    }

    public void config(XrayConfigRequest req) {
        var command = new XrayCtlCommand().config(
                req.host(),
                req.port().orElse(0),
                req.realityHost().orElse(null),
                req.realityPort().orElse(0),
                req.realityNames(),
                req.name().orElse(null),
                req.clean().orElse(false)
        );
        executeCommand(command);
    }

    public StatusResponse status() {
        var result = executeCommand(new XrayCtlCommand().status());
        return parseJson(result, new TypeReference<StatusResponse>() {});
    }

    public void start() {
        executeCommand(new XrayCtlCommand().start());
    }

    public void stop() {
        executeCommand(new XrayCtlCommand().stop());
    }

    public void restart() {
        executeCommand(new XrayCtlCommand().restart());
    }

    public void updateGeodata() {
        executeCommand(new XrayCtlCommand().updateGeodata());
    }

    public void addClients(List<String> names) {
        executeCommand(new XrayCtlCommand().addClients(names));
    }

    public void removeClients(List<String> names) {
        executeCommand(new XrayCtlCommand().removeClients(names));
    }

    public void addOutbound(AddOutboundRequest req) {
        executeCommand(new XrayCtlCommand()
            .addOutbound(
                req.address(),
                req.uuid(),
                req.sni(),
                req.shortId(),
                req.spiderX(),
                req.password(),
                req.port(),
                req.fingerprint().value()
        ));
    }

    public void addOutboundFromUrl(AddOutboundFromUrlRequest req) {
        executeCommand(new XrayCtlCommand().addOutboundFromUrl(req.url(), req.name().orElse(null)));
    }

    public void changeOutbound(String name, ChangeOutboundRequest req) {
        executeCommand(new XrayCtlCommand()
            .changeOutbound(
                name,
                req.address().orElse(null),
                req.uuid().orElse(null),
                req.sni().orElse(null),
                req.shortId().orElse(null),
                req.password().orElse(null),
                req.spiderX().orElse(null),
                req.port().orElse(null),
                req.fingerprint().map(v -> v.value()).orElse(null),
                req.newName().orElse(null)
        ));
    }

    public void removeOutbound(String name) {
        executeCommand(new XrayCtlCommand().removeOutbound(name));
    }

    public void addRoutingRule(AddRoutingRuleRequest req) {
        executeCommand(new XrayCtlCommand().addRoutingRule(
                req.name(),
                req.outbound(),
                req.domain().orElse(null),
                req.ip().orElse(null),
                req.port().orElse(null),
                req.protocol().map(v -> v.value).orElse(null),
                req.priority().orElse(null)
        ));
    }

    public void removeRoutingRule(String name) {
        executeCommand(new XrayCtlCommand().removeRoutingRule(name));
    }

    public void renameRoutingRule(String name, String newName) {
        executeCommand(new XrayCtlCommand().renameRoutingRule(name, newName));
    }

    public void setRulePriority(String name, int priority) {
        executeCommand(new XrayCtlCommand().setRulePriority(name, priority));
    }

    public void changeRuleConditions(String name, ChangeRuleConditionsRequest req) {
        executeCommand(new XrayCtlCommand().changeRuleConditions(
                name,
                req.action().value,
                req.domain().orElse(null),
                req.ip().orElse(null),
                req.port().orElse(null),
                req.protocol().map(v -> v.value()).orElse(null)
        ));
    }

    public void setDomainStrategy(String strategy) {
        executeCommand(new XrayCtlCommand().setDomainStrategy(strategy));
    }

    public XrayVersionsResponse getXrayVersions(int limit) {
        var result = executeCommand(new XrayCtlCommand().getXrayVersions(limit));
        return parseJson(result, new TypeReference<XrayVersionsResponse>() {});
    }

    public void updateXray(String version) {
        executeCommand(new XrayCtlCommand().updateXray(version));
    }

    private String executeCommand(Command command) {
        log.info("Executing xrayctl command: {}", command);
        var result = commandExecutor.execute(command);
        if (result.exitCode() != 0) {
            throw new XrayCtlExecutionException(
                    "xrayctl command failed with exit code " + result.exitCode(),
                    result.stderr()
            );
        }
        return result.stdout();
    }

    private <T> T parseJson(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new XrayCtlExecutionException("Failed to parse xrayctl JSON output: " + e.getMessage(), json, e);
        }
    }
}
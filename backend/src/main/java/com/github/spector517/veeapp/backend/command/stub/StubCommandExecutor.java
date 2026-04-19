package com.github.spector517.veeapp.backend.command.stub;

import com.github.spector517.veeapp.backend.command.Command;
import com.github.spector517.veeapp.backend.command.CommandExecutor;
import com.github.spector517.veeapp.backend.command.CommandResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class StubCommandExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(Command command) {
        log.warn("Skipping command: {}", command);
        return new CommandResult(0, resolveStubOutput(command.toString()), "");
    }

    private String resolveStubOutput(String command) {
        if (!command.contains("--json")) {
            return "Some message";
        }
        if (command.contains("update-xray")) {
            return "{\"releases\": [\"25.12.25\", \"25.11.11\", \"25.10.30\"]}";
        }
        return """
            {
              "veepeenet_version": "v2.4.2",
              "veepeenet_build": 59,
              "xray_version": "25.12.25",
              "server_status": "stopped",
              "enabled": false,
              "restart_required": false,
              "server_host": "127.0.0.1",
              "server_port": "443",
              "reality_address": "ya.ru:443",
              "reality_names": ["ya.ru"],
              "server_name": "dev-server",
              "clients": {
                "clients": [
                  {"name": "client1", "url": "vless://test-uuid@127.0.0.1:443?security=reality#client1@127.0.0.1"},
                  {"name": "client2", "url": "vless://test-uuid@127.0.0.1:443?security=reality#client2@127.0.0.1"}
                ]
              },
              "routing": {
                "domain_strategy": "AsIs",
                "rules": [
                  {"name": "block-ads", "domains": ["geosite:category-ads-all"], "outbound_name": "blackhole", "priority": 10}
                ]
              },
              "outbounds": [
                {"name": "direct"},
                {"name": "blackhole"},
                {"name": "dns"}
              ]
            }
            """;
    }
}


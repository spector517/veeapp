package com.github.spector517.veeapp.backend.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.command")
public class CommandProperties {
    private int timeoutMs = 60000;
}
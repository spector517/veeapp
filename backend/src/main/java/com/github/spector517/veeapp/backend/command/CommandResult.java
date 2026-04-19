package com.github.spector517.veeapp.backend.command;

public record CommandResult(
        int exitCode,
        String stdout,
        String stderr
) {}
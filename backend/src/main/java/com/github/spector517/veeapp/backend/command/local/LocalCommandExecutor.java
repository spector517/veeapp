package com.github.spector517.veeapp.backend.command.local;

import com.github.spector517.veeapp.backend.command.Command;
import com.github.spector517.veeapp.backend.command.CommandExecutionException;
import com.github.spector517.veeapp.backend.command.CommandExecutor;
import com.github.spector517.veeapp.backend.command.CommandProperties;
import com.github.spector517.veeapp.backend.command.CommandResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Profile("!dev")
@RequiredArgsConstructor
public class LocalCommandExecutor implements CommandExecutor {

    private final CommandProperties commandProperties;

    public CommandResult execute(Command command) {
        log.info("Executing local command: {}", command);
        try {
            var process = new ProcessBuilder(command.toArgList())
                    .redirectErrorStream(false)
                    .start();

            var completed = process.waitFor(commandProperties.getTimeoutMs(), TimeUnit.MILLISECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new CommandExecutionException(
                        "Command timed out after %d ms: %s".formatted(commandProperties.getTimeoutMs(), command)
                );
            }

            var stdout = new String(process.getInputStream().readAllBytes()).trim();
            var stderr = new String(process.getErrorStream().readAllBytes()).trim();
            var exitCode = process.exitValue();

            log.debug("Command finished with exit code {}", exitCode);
            return new CommandResult(exitCode, stdout, stderr);
        } catch (IOException e) {
            throw new CommandExecutionException("Failed to execute command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommandExecutionException("Command execution interrupted: " + e.getMessage(), e);
        }
    }
}
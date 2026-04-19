package com.github.spector517.veeapp.backend.controller;

import com.github.spector517.veeapp.backend.dto.shared.response.MessageResponse;
import com.github.spector517.veeapp.backend.dto.shared.response.StatusResponse;
import com.github.spector517.veeapp.backend.service.xrayctl.XrayCtlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xray/service")
@RequiredArgsConstructor
@Tag(
        name = "Xray — Service",
        description = "Control the Xray service lifecycle (start / stop / restart / status)"
)
public class XrayServiceController {

    private final XrayCtlService xrayCtlService;

    @GetMapping("/status")
    @Operation(
            summary = "Get Xray status",
            description = "Run `xrayctl status --json` and return structured status information"
    )
    public StatusResponse status() {
        return xrayCtlService.status();
    }

    @PostMapping("/start")
    @Operation(summary = "Start Xray", description = "Run `xrayctl start` to launch the Xray service")
    public MessageResponse start() {
        xrayCtlService.start();
        return new MessageResponse("Xray started successfully");
    }

    @PostMapping("/stop")
    @Operation(summary = "Stop Xray", description = "Run `xrayctl stop` to shut down the Xray service")
    public MessageResponse stop() {
        xrayCtlService.stop();
        return new MessageResponse("Xray stopped successfully");
    }

    @PostMapping("/restart")
    @Operation(summary = "Restart Xray", description = "Run `xrayctl restart` to restart the Xray service")
    public MessageResponse restart() {
        xrayCtlService.restart();
        return new MessageResponse("Xray restarted successfully");
    }
}
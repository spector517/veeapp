package com.github.spector517.veeapp.backend.controller;

import com.github.spector517.veeapp.backend.dto.shared.request.XrayConfigRequest;
import com.github.spector517.veeapp.backend.dto.shared.response.ConfigurationStateResponse;
import com.github.spector517.veeapp.backend.dto.shared.response.MessageResponse;
import com.github.spector517.veeapp.backend.dto.shared.response.XrayVersionsResponse;
import com.github.spector517.veeapp.backend.service.xrayctl.XrayCtlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xray")
@RequiredArgsConstructor
@Tag(name = "Xray — Config", description = "Configure Xray core settings and geodata")
public class XrayConfigController {

    private final XrayCtlService xrayCtlService;

    @GetMapping("/config")
    @Operation(
            summary = "Check if Xray is configured",
            description = "Run `xrayctl status` to check if Xray service is configured"
    )
    public ConfigurationStateResponse checkConfig() {
        return new ConfigurationStateResponse(xrayCtlService.isConfigured());
    }

    @PostMapping("/config")
    @Operation(
            summary = "Apply Xray configuration",
            description = "Run `xrayctl config` with the provided options (host, port, Reality settings, etc.)"
    )
    public MessageResponse config(@Valid @RequestBody XrayConfigRequest request) {
        xrayCtlService.config(request);
        return new MessageResponse("Xray configured successfully");
    }

    @PutMapping("/update-geodata")
    @Operation(
            summary = "Update geodata",
            description = "Run `xrayctl update-geodata` to refresh GeoIP and GeoSite databases"
    )
    public MessageResponse updateGeodata() {
        xrayCtlService.updateGeodata();
        return new MessageResponse("Geodata updated successfully");
    }

    @GetMapping("/update-xray/versions")
    @Operation(
            summary = "Limited List available Xray versions",
            description = "Fetch a list of available Xray versions for update. The `limit` query parameter controls how many versions to return (default: 5)."
    )
    public XrayVersionsResponse listVersions(
            @RequestParam(required = false, defaultValue = "5") Integer limit
    ) {
        return xrayCtlService.getXrayVersions(limit);
    }

    @PutMapping("/update-xray")
    public MessageResponse updateXray(
            @RequestParam String version
    ) {
        xrayCtlService.updateXray(version);
        return new MessageResponse("Xray updated successfully");
    }
}
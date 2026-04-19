package com.github.spector517.veeapp.backend.controller;

import com.github.spector517.veeapp.backend.dto.shared.request.AddOutboundFromUrlRequest;
import com.github.spector517.veeapp.backend.dto.shared.request.AddOutboundRequest;
import com.github.spector517.veeapp.backend.dto.shared.request.ChangeOutboundRequest;
import com.github.spector517.veeapp.backend.dto.shared.response.MessageResponse;
import com.github.spector517.veeapp.backend.service.xrayctl.XrayCtlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xray/outbounds")
@RequiredArgsConstructor
@Tag(name = "Xray — Outbounds", description = "Manage Xray outbound proxy connections")
public class XrayOutboundController {

    private final XrayCtlService xrayCtlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add outbound",
            description = "Run `xrayctl outbounds add` with full connection parameters"
    )
    public MessageResponse add(@Valid @RequestBody AddOutboundRequest request) {
        xrayCtlService.addOutbound(request);
        return new MessageResponse("Outbound added successfully");
    }

    @PostMapping("/from-url")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add outbound from URL",
            description = "Run `xrayctl outbounds add-from-url` to import an outbound from a share link"
    )
    public MessageResponse addFromUrl(@Valid @RequestBody AddOutboundFromUrlRequest request) {
        xrayCtlService.addOutboundFromUrl(request);
        return new MessageResponse("Outbound added from URL successfully");
    }

    @PatchMapping("/{name}")
    @Operation(
            summary = "Modify outbound",
            description = "Run `xrayctl outbounds change` to update one or more fields of an existing outbound"
    )
    public MessageResponse change(
            @PathVariable String name,
            @Valid @RequestBody ChangeOutboundRequest request
    ) {
        xrayCtlService.changeOutbound(name, request);
        return new MessageResponse("Outbound changed successfully");
    }

    @DeleteMapping("/{name}")
    @Operation(
            summary = "Remove outbound",
            description = "Run `xrayctl outbounds remove` to delete an outbound by name"
    )
    public MessageResponse remove(@PathVariable String name) {
        xrayCtlService.removeOutbound(name);
        return new MessageResponse("Outbound removed successfully");
    }
}
package com.github.spector517.veeapp.backend.controller;

import com.github.spector517.veeapp.backend.dto.shared.request.ClientNamesRequest;
import com.github.spector517.veeapp.backend.dto.shared.response.MessageResponse;
import com.github.spector517.veeapp.backend.service.xrayctl.XrayCtlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xray/clients")
@RequiredArgsConstructor
@Tag(name = "Xray — Clients", description = "Manage Xray VPN client keys")
public class XrayClientController {

    private final XrayCtlService xrayCtlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add clients",
            description = "Run `xrayctl clients add <names...>` to create one or more new client keys"
    )
    public MessageResponse add(@Valid @RequestBody ClientNamesRequest request) {
        xrayCtlService.addClients(request.names());
        return new MessageResponse("Clients added successfully");
    }

    @DeleteMapping
    @Operation(
            summary = "Remove clients",
            description = "Run `xrayctl clients remove <names...>` to delete one or more client keys"
    )
    public MessageResponse remove(@Valid @RequestBody ClientNamesRequest request) {
        xrayCtlService.removeClients(request.names());
        return new MessageResponse("Clients removed successfully");
    }
}
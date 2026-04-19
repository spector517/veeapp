package com.github.spector517.veeapp.backend.controller;

import com.github.spector517.veeapp.backend.dto.shared.request.*;
import com.github.spector517.veeapp.backend.dto.shared.response.MessageResponse;
import com.github.spector517.veeapp.backend.service.xrayctl.XrayCtlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xray/routing")
@RequiredArgsConstructor
@Tag(
        name = "Xray — Routing",
        description = "Manage Xray routing rules and domain strategy"
)
public class XrayRoutingController {

    private final XrayCtlService xrayCtlService;

    @PutMapping("/domain-strategy")
    @Operation(
            summary = "Set domain strategy",
            description = "Run `xrayctl routing set-domain-strategy` to change how domains are resolved (e.g. AsIs, IPIfNonMatch)"
    )
    public MessageResponse setDomainStrategy(@Valid @RequestBody RoutingDomainStrategyRequest request) {
        xrayCtlService.setDomainStrategy(request.strategy().value);
        return new MessageResponse("Domain strategy set successfully");
    }

    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add routing rule",
            description = "Run `xrayctl routing add-rule` to create a new routing rule with an outbound and optional match conditions"
    )
    public MessageResponse addRule(@Valid @RequestBody AddRoutingRuleRequest request) {
        xrayCtlService.addRoutingRule(request);
        return new MessageResponse("Routing rule added successfully");
    }

    @DeleteMapping("/rules/{name}")
    @Operation(
            summary = "Remove routing rule",
            description = "Run `xrayctl routing remove-rule` to delete a routing rule by name"
    )
    public MessageResponse removeRule(@PathVariable String name) {
        xrayCtlService.removeRoutingRule(name);
        return new MessageResponse("Routing rule removed successfully");
    }

    @PatchMapping("/rules/{name}/name")
    @Operation(
            summary = "Rename routing rule",
            description = "Run `xrayctl routing rename-rule` to assign a new name to an existing rule"
    )
    public MessageResponse renameRule(
            @PathVariable String name,
            @Valid @RequestBody RenameRuleRequest request
    ) {
        xrayCtlService.renameRoutingRule(name, request.newName());
        return new MessageResponse("Routing rule renamed successfully");
    }

    @PatchMapping("/rules/{name}/priority")
    @Operation(
            summary = "Set routing rule priority",
            description = "Run `xrayctl routing set-priority` to change the evaluation order of a rule"
    )
    public MessageResponse setPriority(
            @PathVariable String name,
            @Valid @RequestBody SetRulePriorityRequest request
    ) {
        xrayCtlService.setRulePriority(name, request.priority());
        return new MessageResponse("Priority set successfully");
    }

    @PatchMapping("/rules/{name}/conditions")
    @Operation(
            summary = "Change routing rule conditions",
            description = "Run `xrayctl routing change-rule` with action `put` (add/update) or `del` (remove) to modify match conditions (domain, IP, ports, protocol)"
    )
    public MessageResponse changeConditions(
            @PathVariable String name,
            @Valid @RequestBody ChangeRuleConditionsRequest request
    ) {
        xrayCtlService.changeRuleConditions(name, request);
        return new MessageResponse("Rule conditions changed successfully");
    }
}
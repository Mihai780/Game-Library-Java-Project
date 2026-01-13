package com.example.project.controller;

import com.example.project.model.Purchase;
import com.example.project.model.PurchaseCreateRequestDTO;
import com.example.project.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/purchases")
@Tag(name = "Purchase Management", description = "APIs for purchasing games and viewing purchase history.")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Operation(summary = "Purchase a game", description = "Creates a purchase record for a user and a game.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Purchase created successfully."),
            @ApiResponse(responseCode = "404", description = "User or Game not found."),
            @ApiResponse(responseCode = "409", description = "User already purchased this game."),
            @ApiResponse(responseCode = "400", description = "Validation failed.")
    })
    @PostMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<Purchase> purchaseGame(
            @PathVariable Long userId,
            @PathVariable Long gameId,
            @Valid @RequestBody PurchaseCreateRequestDTO request) {

        Purchase created = purchaseService.create(userId, gameId, request.getPriceCents());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get purchase history by user", description = "Returns all purchases made by the specified user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Purchases returned."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Purchase>> getPurchasesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(purchaseService.getByUser(userId));
    }
}

package com.rotalog.api.controller;

import com.rotalog.api.dto.InventoryDTO;
import com.rotalog.api.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryDTO.Response>> listAll() {
        return ResponseEntity.ok(inventoryService.listAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO.Response> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.findByProduct(productId));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO.Response>> listLowStock() {
        return ResponseEntity.ok(inventoryService.listLowStock());
    }

    @PostMapping
    public ResponseEntity<InventoryDTO.Response> create(@Valid @RequestBody InventoryDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(request));
    }

    @PostMapping("/movement")
    public ResponseEntity<InventoryDTO.MovementResponse> move(
            @Valid @RequestBody InventoryDTO.MovementRequest request) {
        return ResponseEntity.ok(inventoryService.move(request));
    }

    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<InventoryDTO.MovementResponse>> listMovements(
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.listMovementsByProduct(productId));
    }
}

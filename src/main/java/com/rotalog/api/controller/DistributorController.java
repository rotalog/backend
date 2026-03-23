package com.rotalog.api.controller;

import com.rotalog.api.dto.DistributorDTO;
import com.rotalog.api.service.DistributorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distributors")
@RequiredArgsConstructor
public class DistributorController {

    private final DistributorService distributorService;

    @GetMapping
    public ResponseEntity<List<DistributorDTO.Response>> listAll(
            @RequestParam(required = false) Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return ResponseEntity.ok(distributorService.listActive());
        }
        return ResponseEntity.ok(distributorService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistributorDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(distributorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DistributorDTO.Response> create(
            @Valid @RequestBody DistributorDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distributorService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistributorDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody DistributorDTO.Request request) {
        return ResponseEntity.ok(distributorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        distributorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DistributorDTO.Response> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(distributorService.toggleStatus(id));
    }
}

package com.rotalog.api.controller;

import com.rotalog.api.dto.ProductDTO;
import com.rotalog.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO.Response>> listAll(
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) String category) {

        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(productService.findByCategory(category));
        }
        if (Boolean.TRUE.equals(activeOnly)) {
            return ResponseEntity.ok(productService.listActive());
        }
        return ResponseEntity.ok(productService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO.Response> create(@Valid @RequestBody ProductDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO.Request request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductDTO.Response> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleStatus(id));
    }
}

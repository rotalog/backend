package com.rotalog.api.controller;

import com.rotalog.api.dto.OrderDTO;
import com.rotalog.api.model.Order;
import com.rotalog.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rotalog/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO.Response>> listAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long distributorId,
            @RequestParam(required = false) Order.OrderStatus status) {

        if (customerId != null) {
            return ResponseEntity.ok(orderService.findByCustomer(customerId));
        }
        if (distributorId != null) {
            return ResponseEntity.ok(orderService.findByDistributor(distributorId));
        }
        if (status != null) {
            return ResponseEntity.ok(orderService.findByStatus(status));
        }
        return ResponseEntity.ok(orderService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDTO.Response> create(@Valid @RequestBody OrderDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO.Response> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderDTO.StatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}

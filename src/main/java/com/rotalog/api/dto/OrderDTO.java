package com.rotalog.api.dto;

import com.rotalog.api.model.Order;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Customer is required")
        private Long customerId;

        @NotNull(message = "Distributor is required")
        private Long distributorId;

        @Size(max = 500, message = "Notes must have at most 500 characters")
        private String notes;

        @NotEmpty(message = "Order must have at least one item")
        private List<OrderItemRequest> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotNull(message = "Product is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than zero")
        private BigDecimal unitPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusRequest {
        @NotNull(message = "Status is required")
        private Order.OrderStatus status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long customerId;
        private String customerName;
        private Long distributorId;
        private String distributorName;
        private Order.OrderStatus status;
        private String notes;
        private BigDecimal totalAmount;
        private List<OrderItemResponse> items;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Order p) {
            return Response.builder()
                    .id(p.getId())
                    .customerId(p.getCustomer().getId())
                    .customerName(p.getCustomer().getName())
                    .distributorId(p.getDistributor().getId())
                    .distributorName(p.getDistributor().getLegalName())
                    .status(p.getStatus())
                    .notes(p.getNotes())
                    .totalAmount(p.getTotalAmount())
                    .items(p.getItems().stream().map(OrderItemResponse::fromEntity).toList())
                    .createdAt(p.getCreatedAt())
                    .updatedAt(p.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public static OrderItemResponse fromEntity(com.rotalog.api.model.OrderItem item) {
            return OrderItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .build();
        }
    }
}

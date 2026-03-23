package com.rotalog.api.dto;

import com.rotalog.api.model.Inventory;
import com.rotalog.api.model.InventoryMovement;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class InventoryDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Product is required")
        private Long productId;

        @NotNull(message = "Initial quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer currentQuantity;

        @Min(value = 0, message = "Minimum quantity cannot be negative")
        private Integer minimumQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MovementRequest {
        @NotNull(message = "Product is required")
        private Long productId;

        @NotNull(message = "Movement type is required")
        private InventoryMovement.MovementType type;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @Size(max = 300, message = "Reason must have at most 300 characters")
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long productId;
        private String productName;
        private Integer currentQuantity;
        private Integer minimumQuantity;
        private Boolean lowStock;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Inventory e) {
            return Response.builder()
                    .id(e.getId())
                    .productId(e.getProduct().getId())
                    .productName(e.getProduct().getName())
                    .currentQuantity(e.getCurrentQuantity())
                    .minimumQuantity(e.getMinimumQuantity())
                    .lowStock(e.getCurrentQuantity() <= e.getMinimumQuantity())
                    .createdAt(e.getCreatedAt())
                    .updatedAt(e.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MovementResponse {
        private Long id;
        private Long productId;
        private String productName;
        private InventoryMovement.MovementType type;
        private Integer quantity;
        private String reason;
        private Integer previousBalance;
        private Integer currentBalance;
        private Long orderId;
        private LocalDateTime createdAt;

        public static MovementResponse fromEntity(InventoryMovement m) {
            return MovementResponse.builder()
                    .id(m.getId())
                    .productId(m.getProduct().getId())
                    .productName(m.getProduct().getName())
                    .type(m.getType())
                    .quantity(m.getQuantity())
                    .reason(m.getReason())
                    .previousBalance(m.getPreviousBalance())
                    .currentBalance(m.getCurrentBalance())
                    .orderId(m.getOrder() != null ? m.getOrder().getId() : null)
                    .createdAt(m.getCreatedAt())
                    .build();
        }
    }
}

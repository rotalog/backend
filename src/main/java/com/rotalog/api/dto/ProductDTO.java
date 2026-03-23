package com.rotalog.api.dto;

import com.rotalog.api.model.Product;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must have at most 150 characters")
        private String name;

        @Size(max = 500, message = "Description must have at most 500 characters")
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        private BigDecimal price;

        @Size(max = 100, message = "Category must have at most 100 characters")
        private String category;

        @Size(max = 50, message = "Unit of measure must have at most 50 characters")
        private String unitOfMeasure;

        private Boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String category;
        private String unitOfMeasure;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Product p) {
            return Response.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .description(p.getDescription())
                    .price(p.getPrice())
                    .category(p.getCategory())
                    .unitOfMeasure(p.getUnitOfMeasure())
                    .active(p.getActive())
                    .createdAt(p.getCreatedAt())
                    .updatedAt(p.getUpdatedAt())
                    .build();
        }
    }
}

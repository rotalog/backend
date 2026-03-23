package com.rotalog.api.dto;

import com.rotalog.api.model.Customer;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must have at most 150 characters")
        private String name;

        @Size(max = 14, message = "Tax ID is invalid")
        private String taxId;

        private Customer.CustomerType type;

        @Email(message = "Email is invalid")
        @Size(max = 150, message = "Email must have at most 150 characters")
        private String email;

        @Size(max = 20, message = "Phone must have at most 20 characters")
        private String phone;

        @Size(max = 200, message = "Address must have at most 200 characters")
        private String address;

        @Size(max = 100, message = "City must have at most 100 characters")
        private String city;

        @Size(min = 2, max = 2, message = "State must have exactly 2 characters")
        private String state;

        private LocalDate birthDate;

        private Boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String taxId;
        private Customer.CustomerType type;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private LocalDate birthDate;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Customer c) {
            return Response.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .taxId(c.getTaxId())
                    .type(c.getType())
                    .email(c.getEmail())
                    .phone(c.getPhone())
                    .address(c.getAddress())
                    .city(c.getCity())
                    .state(c.getState())
                    .birthDate(c.getBirthDate())
                    .active(c.getActive())
                    .createdAt(c.getCreatedAt())
                    .updatedAt(c.getUpdatedAt())
                    .build();
        }
    }
}

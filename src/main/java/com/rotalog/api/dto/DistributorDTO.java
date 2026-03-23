package com.rotalog.api.dto;

import com.rotalog.api.model.Distributor;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class DistributorDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Legal name is required")
        @Size(max = 150, message = "Legal name must have at most 150 characters")
        private String legalName;

        @Size(max = 150, message = "Trade name must have at most 150 characters")
        private String tradeName;

        @Size(max = 18, message = "Tax ID is invalid")
        private String taxId;

        @Size(max = 200, message = "Address must have at most 200 characters")
        private String address;

        @Size(max = 100, message = "City must have at most 100 characters")
        private String city;

        @Size(min = 2, max = 2, message = "State must have exactly 2 characters")
        private String state;

        @Size(max = 20, message = "Phone must have at most 20 characters")
        private String phone;

        @Email(message = "Email is invalid")
        @Size(max = 150, message = "Email must have at most 150 characters")
        private String email;

        @Size(max = 200, message = "Contact person must have at most 200 characters")
        private String contactPerson;

        private Boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String legalName;
        private String tradeName;
        private String taxId;
        private String address;
        private String city;
        private String state;
        private String phone;
        private String email;
        private String contactPerson;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(Distributor d) {
            return Response.builder()
                    .id(d.getId())
                    .legalName(d.getLegalName())
                    .tradeName(d.getTradeName())
                    .taxId(d.getTaxId())
                    .address(d.getAddress())
                    .city(d.getCity())
                    .state(d.getState())
                    .phone(d.getPhone())
                    .email(d.getEmail())
                    .contactPerson(d.getContactPerson())
                    .active(d.getActive())
                    .createdAt(d.getCreatedAt())
                    .updatedAt(d.getUpdatedAt())
                    .build();
        }
    }
}

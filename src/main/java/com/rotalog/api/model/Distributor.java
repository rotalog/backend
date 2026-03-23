package com.rotalog.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "distributors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Distributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String legalName;

    @Column(length = 150)
    private String tradeName;

    @Column(unique = true, length = 18)
    private String taxId;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(length = 20)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String contactPerson;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

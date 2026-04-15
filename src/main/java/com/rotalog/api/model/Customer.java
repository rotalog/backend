package com.rotalog.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, length = 14)
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CustomerType type = CustomerType.INDIVIDUAL;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Localização geográfica do cliente.
     * Tipo Point do PostGIS (SRID 4326 = WGS84 / coordenadas GPS).
     * Exemplo de uso:
     *   GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
     *   Point p = gf.createPoint(new Coordinate(longitude, latitude));
     *   customer.setLocation(p);
     */
    @Column(columnDefinition = "GEOGRAPHY(POINT, 4326)")
    private Point location;

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

    public enum CustomerType {
        INDIVIDUAL, COMPANY
    }
}

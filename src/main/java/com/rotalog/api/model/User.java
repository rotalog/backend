package com.rotalog.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidade de usuário do sistema.
 * Implementa UserDetails para integração direta com Spring Security.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    /** Usado como "username" no login */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Senha armazenada com BCrypt */
    @Column(nullable = false)
    private String password;

//    Será usado futuramente, assim que a lógica de permissão for adicionada à aplicação
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.OPERATOR;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt  = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------
    // UserDetails — Spring Security
    // -------------------------------------------------------

//    Será usado futuramente, assim que a lógica de permissão for adicionada à aplicação
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /** Spring Security usa este campo como identificador único */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired()    { return true; }

    @Override
    public boolean isAccountNonLocked()     { return true; }

    @Override
    public boolean isCredentialsNonExpired(){ return true; }

    @Override
    public boolean isEnabled()              { return Boolean.TRUE.equals(active); }

//    Será usado futuramente, assim que a lógica de permissão for adicionada à aplicação
// -------------------------------------------------------
// Roles disponíveis no sistema
// -------------------------------------------------------
    public enum Role {
        /** Acesso total: criar usuários, configurar sistema */
        ADMIN,
        /** Acesso de leitura e operações normais */
        OPERATOR,
        /** Somente leitura */
        VIEWER
    }
}

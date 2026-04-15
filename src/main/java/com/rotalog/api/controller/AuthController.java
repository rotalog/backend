package com.rotalog.api.controller;

import com.rotalog.api.dto.AuthDTO;
import com.rotalog.api.model.User;
import com.rotalog.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticação — todos públicos (configurado no SecurityConfig).
 *
 * POST /api/auth/register  → cria conta e retorna token
 * POST /api/auth/login     → autentica e retorna token
 * GET  /api/auth/me        → retorna dados do usuário logado
 */
@RestController
@RequestMapping("/rotalog/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registra um novo usuário.
     * Apenas ADMIN pode registrar usuários com role ADMIN.
     * Qualquer pessoa pode se registrar como OPERATOR (padrão).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthDTO.AuthResponse> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    /**
     * Autentica um usuário e retorna o token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Retorna os dados do usuário autenticado (token válido necessário).
     */
    @GetMapping("/me")
    public ResponseEntity<AuthDTO.UserInfo> me(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(AuthDTO.UserInfo.from(user));
    }
}

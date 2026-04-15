package com.rotalog.api.service;

import com.rotalog.api.dto.AuthDTO;
import com.rotalog.api.exception.BusinessException;
import com.rotalog.api.model.User;
import com.rotalog.api.repository.UserRepository;
import com.rotalog.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final AuthenticationManager authenticationManager;

    // ----------------------------------------------------------
    // Registro
    // ----------------------------------------------------------

    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : User.Role.OPERATOR)
                .active(true)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthDTO.AuthResponse.of(token, user);
    }

    // ----------------------------------------------------------
    // Login
    // ----------------------------------------------------------

    @Transactional(readOnly = true)
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        // Delega ao AuthenticationManager (valida email + senha + status ativo)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtService.generateToken(user);
        return AuthDTO.AuthResponse.of(token, user);
    }
}

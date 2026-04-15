package com.rotalog.api.dto;

import com.rotalog.api.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class AuthDTO {

    // -------------------------------------------------------
    // Login
    // -------------------------------------------------------
    @Data
    public static class LoginRequest {

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        private String password;
    }

    // -------------------------------------------------------
    // Registro
    // -------------------------------------------------------
    @Data
    public static class RegisterRequest {

        @NotBlank(message = "Nome é obrigatório")
        private String name;

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        private String password;

        /** Se omitido, o padrão é OPERATOR */
        private User.Role role;
    }

    // -------------------------------------------------------
    // Resposta de autenticação (login e registro)
    // -------------------------------------------------------
    @Data
    @Builder
    public static class AuthResponse {
        private String token;
        private String type;
        private Long   userId;
        private String name;
        private String email;
        private String role;

        public static AuthResponse of(String token, User user) {
            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();
        }
    }

    // -------------------------------------------------------
    // Resposta de informações do usuário logado
    // -------------------------------------------------------
    @Data
    @Builder
    public static class UserInfo {
        private Long   id;
        private String name;
        private String email;
        private String role;
        private Boolean active;

        public static UserInfo from(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .active(user.getActive())
                    .build();
        }
    }
}

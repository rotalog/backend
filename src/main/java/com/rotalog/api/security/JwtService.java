package com.rotalog.api.security;

import com.rotalog.api.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * Responsável por gerar, validar e extrair dados de tokens JWT.
 *
 * Configuração no application.properties:
 *   security.jwt.secret=<string Base64 com no mínimo 256 bits>
 *   security.jwt.expiration-ms=86400000   # 24h em milissegundos
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // ----------------------------------------------------------
    // Geração
    // ----------------------------------------------------------

    /**
     * Gera um token JWT para o usuário autenticado.
     * Claims incluídos: sub (email), name, role.
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(Map.of(
                        "name", user.getName(),
                        "role", user.getRole().name()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ----------------------------------------------------------
    // Extração
    // ----------------------------------------------------------

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    // ----------------------------------------------------------
    // Validação
    // ----------------------------------------------------------

    /**
     * Retorna true se o token é válido e pertence ao usuário informado.
     */
    public boolean isTokenValid(String token, User user) {
        final String email = extractEmail(token);
        return email.equals(user.getEmail()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ----------------------------------------------------------
    // Interno
    // ----------------------------------------------------------

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

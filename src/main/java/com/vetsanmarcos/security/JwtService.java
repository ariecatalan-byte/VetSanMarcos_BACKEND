package com.vetsanmarcos.security;

import com.vetsanmarcos.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** Genera y valida access tokens JWT firmados (HMAC). Valida firma y vigencia al parsear. */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-expiration-ms}") long accessExpirationMs) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres.");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.accessExpirationMs = accessExpirationMs;
    }

    public String generarAccessToken(Usuario u) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .subject(u.getCorreo())
                .claim("rol", u.getRol().name())
                .claim("uid", u.getId())
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + accessExpirationMs))
                .signWith(key)
                .compact();
    }

    /** Lanza JwtException si la firma es inválida, el token está mal formado o ya expiró. */
    public Claims parsear(String token) throws JwtException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getAccessExpirationSeconds() {
        return accessExpirationMs / 1000;
    }
}

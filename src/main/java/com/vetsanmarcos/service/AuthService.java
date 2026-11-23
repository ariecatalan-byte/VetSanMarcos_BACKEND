package com.vetsanmarcos.service;

import com.vetsanmarcos.dto.*;
import com.vetsanmarcos.entity.RefreshToken;
import com.vetsanmarcos.entity.Rol;
import com.vetsanmarcos.entity.Usuario;
import com.vetsanmarcos.exception.ApiException;
import com.vetsanmarcos.repository.RefreshTokenRepository;
import com.vetsanmarcos.repository.UsuarioRepository;
import com.vetsanmarcos.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-expiration-days}")
    private long refreshDias;

    /** noRollbackFor: el contador de intentos fallidos debe guardarse aunque se lance la excepción 401. */
    @Transactional(noRollbackFor = ApiException.class)
    public AuthResponse login(LoginRequest req) {
        Usuario u = usuarioRepository.findByCorreoIgnoreCase(ReglasUsuario.normalizarCorreo(req.correo()))
                .orElseThrow(() -> ApiException.noAutorizado("Correo o contraseña incorrectos."));

        if (Boolean.FALSE.equals(u.getActivo())) {
            throw ApiException.noAutorizado("Tu cuenta está desactivada. Contacta al administrador.");
        }
        if (Boolean.TRUE.equals(u.getBloqueado())) {
            throw new ApiException(HttpStatus.LOCKED, "Tu cuenta está bloqueada por múltiples intentos fallidos. Contacta al administrador.");
        }
        if (!passwordEncoder.matches(req.password(), u.getPassword())) {
            u.setIntentosFallidos(u.getIntentosFallidos() + 1);
            if (u.getIntentosFallidos() >= ReglasUsuario.MAX_INTENTOS) {
                u.setBloqueado(true);
                usuarioRepository.save(u);
                throw new ApiException(HttpStatus.LOCKED, "Has superado el máximo de intentos permitidos. Tu cuenta ha sido bloqueada.");
            }
            usuarioRepository.save(u);
            throw ApiException.noAutorizado("Correo o contraseña incorrectos. Intentos restantes: "
                    + (ReglasUsuario.MAX_INTENTOS - u.getIntentosFallidos()) + ".");
        }
        u.setIntentosFallidos(0);
        usuarioRepository.save(u);
        return emitir(u);
    }

    /** Registro público: SIEMPRE crea un cliente (el rol que llegue se ignora). */
    @Transactional
    public AuthResponse registrar(UsuarioRequest req) {
        ReglasUsuario.validarCorreo(req.correo());
        ReglasUsuario.validarEdad(req.fechaNacimiento());
        ReglasUsuario.validarPassword(req.password());
        String correo = ReglasUsuario.normalizarCorreo(req.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw ApiException.conflicto("Ya existe una cuenta con ese correo.");
        }
        Usuario nuevo = usuarioRepository.save(Usuario.builder()
                .nombre(req.nombre().trim()).apellido(req.apellido().trim())
                .fechaNacimiento(req.fechaNacimiento()).genero(req.genero()).correo(correo)
                .password(passwordEncoder.encode(req.password()))
                .rol(Rol.CLIENTE).direccion(req.direccion().trim()).region(req.region()).comuna(req.comuna())
                .build());
        return emitir(nuevo);
    }

    /** Renovación con ROTACIÓN: el refresh usado se revoca y se entrega uno nuevo. Reutilizar uno revocado = posible robo → se revocan todos. */
    @Transactional(noRollbackFor = ApiException.class)
    public AuthResponse refrescar(String refreshToken) {
        RefreshToken guardado = refreshRepository.findByTokenHash(hash(refreshToken))
                .orElseThrow(() -> ApiException.noAutorizado("Sesión inválida. Inicia sesión nuevamente."));
        Usuario u = guardado.getUsuario();

        if (Boolean.TRUE.equals(guardado.getRevocado())) {
            refreshRepository.revocarTodosDe(u.getId());
            throw ApiException.noAutorizado("Sesión inválida. Inicia sesión nuevamente.");
        }
        if (guardado.getExpiraEn().isBefore(Instant.now())) {
            throw ApiException.noAutorizado("Tu sesión expiró. Inicia sesión nuevamente.");
        }
        if (Boolean.FALSE.equals(u.getActivo()) || Boolean.TRUE.equals(u.getBloqueado())) {
            throw ApiException.noAutorizado("Tu cuenta no está disponible.");
        }
        guardado.setRevocado(true);
        refreshRepository.save(guardado);
        return emitir(u);
    }

    @Transactional
    public void cerrarSesion(String refreshToken) {
        refreshRepository.findByTokenHash(hash(refreshToken)).ifPresent(t -> {
            t.setRevocado(true);
            refreshRepository.save(t);
        });
    }

    private AuthResponse emitir(Usuario u) {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshRepository.save(RefreshToken.builder()
                .tokenHash(hash(refresh)).usuario(u).creadoEn(Instant.now())
                .expiraEn(Instant.now().plus(refreshDias, ChronoUnit.DAYS)).build());
        return new AuthResponse(jwtService.generarAccessToken(u), refresh, "Bearer",
                jwtService.getAccessExpirationSeconds(), Mapeador.aDto(u));
    }

    static String hash(String valor) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

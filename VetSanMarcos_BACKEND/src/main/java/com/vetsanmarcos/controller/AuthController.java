package com.vetsanmarcos.controller;

import com.vetsanmarcos.dto.AuthResponse;
import com.vetsanmarcos.dto.LoginRequest;
import com.vetsanmarcos.entity.Rol;
import com.vetsanmarcos.entity.Usuario;
import com.vetsanmarcos.repository.UsuarioRepository;
import com.vetsanmarcos.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            return ResponseEntity.status(401).body(Map.of("error", "Cuenta bloqueada. Contacte al administrador."));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= 3) {
                usuario.setBloqueado(true);
            }
            usuarioRepository.save(usuario);
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        // Login exitoso: resetear intentos
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }
        Usuario u = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "nombre", u.getNombre(),
                "email", u.getEmail(),
                "rol", u.getRol()
        ));
    }

    // Registro básico de cliente (puedes expandir con más campos)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (usuarioRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email ya registrado"));
        }
        Usuario nuevo = Usuario.builder()
                .nombre(body.getOrDefault("nombre", "Cliente"))
                .email(email)
                .password(passwordEncoder.encode(body.get("password")))
                .rol(Rol.CLIENTE)
                .build();
        usuarioRepository.save(nuevo);
        return ResponseEntity.status(201).body(Map.of("mensaje", "Cliente registrado correctamente"));
    }
}

package com.vetsanmarcos.security;

import com.vetsanmarcos.entity.Usuario;
import com.vetsanmarcos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (Boolean.TRUE.equals(u.getBloqueado()) || Boolean.FALSE.equals(u.getActivo())) {
            throw new UsernameNotFoundException("Cuenta no disponible");
        }
        // El rol se lee SIEMPRE de la base de datos: si un admin cambia un rol, aplica de inmediato.
        return new User(u.getCorreo(), u.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name())));
    }
}

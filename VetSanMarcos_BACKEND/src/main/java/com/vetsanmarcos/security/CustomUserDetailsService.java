package com.vetsanmarcos.security;

import com.vetsanmarcos.entity.Usuario;
import com.vetsanmarcos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        if (Boolean.TRUE.equals(u.getBloqueado())) {
            throw new UsernameNotFoundException("Cuenta bloqueada");
        }
        if (Boolean.FALSE.equals(u.getActivo())) {
            throw new UsernameNotFoundException("Cuenta inactiva");
        }

        return new User(
                u.getEmail(),
                u.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name()))
        );
    }
}

package com.vetsanmarcos.service;

import com.vetsanmarcos.dto.*;
import com.vetsanmarcos.entity.Rol;
import com.vetsanmarcos.entity.Usuario;
import com.vetsanmarcos.exception.ApiException;
import com.vetsanmarcos.repository.OrdenRepository;
import com.vetsanmarcos.repository.RefreshTokenRepository;
import com.vetsanmarcos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final OrdenRepository ordenRepository;
    private final RefreshTokenRepository refreshRepository;
    private final PasswordEncoder passwordEncoder;

    /** Usuario autenticado actual, leído desde la base de datos por el correo del token. */
    @Transactional(readOnly = true)
    public Usuario actor(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> ApiException.noAutorizado("Sesión inválida."));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse yo(String correo) {
        return Mapeador.aDto(actor(correo));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId())).map(Mapeador::aDto).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return Mapeador.aDto(buscar(id));
    }

    /** ADMIN crea cualquier rol; VENDEDOR solo clientes. */
    public UsuarioResponse crear(UsuarioRequest req, String correoActor) {
        Usuario actor = actor(correoActor);
        Rol rol = Rol.desdeFrontend(req.rol());
        if (rol == null) rol = Rol.CLIENTE;
        if (actor.getRol() != Rol.ADMIN && rol != Rol.CLIENTE) {
            throw ApiException.prohibido("Tu rol solo permite crear usuarios con rol cliente.");
        }
        ReglasUsuario.validarCorreo(req.correo());
        ReglasUsuario.validarEdad(req.fechaNacimiento());
        ReglasUsuario.validarPassword(req.password());
        String correo = ReglasUsuario.normalizarCorreo(req.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw ApiException.conflicto("Ya existe una cuenta con ese correo.");
        }
        Usuario u = Usuario.builder()
                .nombre(req.nombre().trim()).apellido(req.apellido().trim()).fechaNacimiento(req.fechaNacimiento())
                .genero(req.genero()).correo(correo).password(passwordEncoder.encode(req.password()))
                .rol(rol).direccion(req.direccion().trim()).region(req.region()).comuna(req.comuna()).build();
        return Mapeador.aDto(usuarioRepository.save(u));
    }

    /** Solo ADMIN. Nadie puede cambiarse su propio rol; siempre debe quedar al menos un administrador activo. */
    public UsuarioResponse actualizar(Long id, UsuarioRequest req, String correoActor) {
        Usuario actor = actor(correoActor);
        Usuario u = buscar(id);
        ReglasUsuario.validarCorreo(req.correo());
        ReglasUsuario.validarEdad(req.fechaNacimiento());
        String correo = ReglasUsuario.normalizarCorreo(req.correo());
        if (!correo.equalsIgnoreCase(u.getCorreo()) && usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw ApiException.conflicto("Ya existe otra cuenta con ese correo.");
        }
        Rol nuevoRol = Rol.desdeFrontend(req.rol());
        if (nuevoRol != null && !nuevoRol.equals(u.getRol()) && !actor.getId().equals(u.getId())) {
            if (u.getRol() == Rol.ADMIN && usuarioRepository.countByRolAndActivoTrue(Rol.ADMIN) <= 1) {
                throw ApiException.conflicto("Debe existir al menos un administrador.");
            }
            u.setRol(nuevoRol);
        }
        if (req.password() != null && !req.password().isEmpty()) {
            ReglasUsuario.validarPassword(req.password());
            u.setPassword(passwordEncoder.encode(req.password()));
        }
        u.setNombre(req.nombre().trim());
        u.setApellido(req.apellido().trim());
        u.setFechaNacimiento(req.fechaNacimiento());
        u.setGenero(req.genero());
        u.setCorreo(correo);
        u.setDireccion(req.direccion().trim());
        u.setRegion(req.region());
        u.setComuna(req.comuna());
        return Mapeador.aDto(usuarioRepository.save(u));
    }

    /** Cualquier usuario autenticado edita SOLO su propio perfil (sin rol ni contraseña). */
    public UsuarioResponse actualizarPerfil(PerfilRequest req, String correoActor) {
        Usuario u = actor(correoActor);
        ReglasUsuario.validarCorreo(req.correo());
        ReglasUsuario.validarEdad(req.fechaNacimiento());
        String correo = ReglasUsuario.normalizarCorreo(req.correo());
        if (!correo.equalsIgnoreCase(u.getCorreo()) && usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw ApiException.conflicto("Ya existe otra cuenta con ese correo.");
        }
        u.setNombre(req.nombre().trim());
        u.setApellido(req.apellido().trim());
        u.setFechaNacimiento(req.fechaNacimiento());
        u.setGenero(req.genero());
        u.setCorreo(correo);
        u.setDireccion(req.direccion().trim());
        u.setRegion(req.region());
        u.setComuna(req.comuna());
        return Mapeador.aDto(usuarioRepository.save(u));
    }

    /** Si el usuario ya tiene compras se DESACTIVA (conserva el historial); si no, se elimina. */
    public void eliminar(Long id, String correoActor) {
        Usuario actor = actor(correoActor);
        Usuario u = buscar(id);
        if (actor.getId().equals(u.getId())) {
            throw ApiException.conflicto("No puedes eliminar tu propia cuenta.");
        }
        if (u.getRol() == Rol.ADMIN && Boolean.TRUE.equals(u.getActivo()) && usuarioRepository.countByRolAndActivoTrue(Rol.ADMIN) <= 1) {
            throw ApiException.conflicto("Debe existir al menos un administrador.");
        }
        refreshRepository.eliminarTodosDe(u.getId());
        if (ordenRepository.existsByUsuarioId(u.getId())) {
            u.setActivo(false);
            usuarioRepository.save(u);
        } else {
            usuarioRepository.delete(u);
        }
    }

    public UsuarioResponse desbloquear(Long id) {
        Usuario u = buscar(id);
        u.setBloqueado(false);
        u.setIntentosFallidos(0);
        return Mapeador.aDto(usuarioRepository.save(u));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> ApiException.noEncontrado("El usuario no existe."));
    }
}

package com.vetsanmarcos.config;

import com.vetsanmarcos.entity.*;
import com.vetsanmarcos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) return;

        // Usuarios de prueba
        usuarioRepository.save(Usuario.builder()
                .nombre("Administrador")
                .email("admin@duoc.cl")
                .password(passwordEncoder.encode("Admin1234"))
                .rol(Rol.ADMIN)
                .build());

        usuarioRepository.save(Usuario.builder()
                .nombre("Vendedor Demo")
                .email("vendedor@duoc.cl")
                .password(passwordEncoder.encode("Vendedor1234"))
                .rol(Rol.VENDEDOR)
                .build());

        usuarioRepository.save(Usuario.builder()
                .nombre("Cliente Demo")
                .email("cliente@duoc.cl")
                .password(passwordEncoder.encode("Cliente1234"))
                .rol(Rol.CLIENTE)
                .build());

        // Categorías y productos de ejemplo
        Categoria alimento = categoriaRepository.save(Categoria.builder()
                .nombre("Alimento").descripcion("Comida para mascotas").build());
        Categoria juguetes = categoriaRepository.save(Categoria.builder()
                .nombre("Juguetes").descripcion("Juguetes y entretenimiento").build());
        Categoria higiene = categoriaRepository.save(Categoria.builder()
                .nombre("Higiene").descripcion("Productos de aseo").build());

        productoRepository.save(Producto.builder()
                .nombre("Alimento Premium Perro 15kg")
                .descripcion("Alimento balanceado para perros adultos")
                .precio(new BigDecimal("45990"))
                .stock(25)
                .descuento(new BigDecimal("10"))
                .categoria(alimento)
                .imagenUrl("https://via.placeholder.com/300")
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Pelota de goma resistente")
                .descripcion("Juguete durable para perros")
                .precio(new BigDecimal("7990"))
                .stock(50)
                .categoria(juguetes)
                .imagenUrl("https://via.placeholder.com/300")
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Shampoo hipoalergénico 500ml")
                .descripcion("Para pieles sensibles")
                .precio(new BigDecimal("12990"))
                .stock(30)
                .categoria(higiene)
                .imagenUrl("https://via.placeholder.com/300")
                .build());

        System.out.println(">>> Datos de prueba cargados (admin@duoc.cl / Admin1234, etc.)");
    }
}

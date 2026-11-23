package com.vetsanmarcos.config;

import com.vetsanmarcos.entity.*;
import com.vetsanmarcos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Datos de prueba para la demostración. Solo se cargan si la base de datos está VACÍA (no pisa datos existentes),
 * así que al reiniciar la app lo que se creó/modificó sigue ahí (persistencia real en la base de datos).
 *
 * Cuentas (una por rol): admin@duoc.cl / Admin1234 · vendedor@duoc.cl / Vendedor1234 · cliente@duoc.cl / Cliente1234
 * Clientes extra con la misma clave "Cliente1234".
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final OrdenRepository ordenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean habilitado;

    @Override
    public void run(String... args) {
        if (!habilitado || usuarioRepository.count() > 0) return;

        // ---------------- usuarios ----------------
        usuario("Admin", "San Marcos", "1990-05-10", "Prefiero no decirlo", "admin@duoc.cl", "Admin1234", Rol.ADMIN, "Av. Principal 123", "Santiago");
        usuario("Valentina", "Rojas", "1995-08-21", "Femenino", "vendedor@duoc.cl", "Vendedor1234", Rol.VENDEDOR, "Calle Los Alerces 456", "Providencia");
        List<Usuario> clientes = new ArrayList<>();
        clientes.add(usuario("Matías", "Soto", "2001-03-14", "Masculino", "cliente@duoc.cl", "Cliente1234", Rol.CLIENTE, "Pasaje Las Rosas 789", "Puente Alto"));
        clientes.add(usuario("Camila", "Fuentes", "1998-11-02", "Femenino", "camila.fuentes@duoc.cl", "Cliente1234", Rol.CLIENTE, "Av. Apoquindo 3400", "Las Condes"));
        clientes.add(usuario("Diego", "Pérez", "1993-07-19", "Masculino", "diego.perez@duoc.cl", "Cliente1234", Rol.CLIENTE, "Los Olmos 120", "Maipú"));
        clientes.add(usuario("Javiera", "Muñoz", "2003-01-27", "Femenino", "javiera.munoz@duoc.cl", "Cliente1234", Rol.CLIENTE, "Calle Nueva 55", "Ñuñoa"));
        clientes.add(usuario("Tomás", "Araya", "1989-09-05", "Otro", "tomas.araya@duoc.cl", "Cliente1234", Rol.CLIENTE, "Av. Matta 900", "Santiago"));

        // ---------------- categorías y productos (mismo catálogo de EP2) ----------------
        Map<String, Categoria> cat = new HashMap<>();
        cat.put("Antibioticos", categoriaRepository.save(Categoria.builder().nombre("Antibióticos").descripcion("Medicamentos para tratar infecciones bacterianas en perros y gatos.").build()));
        cat.put("Antiparasitarios", categoriaRepository.save(Categoria.builder().nombre("Antiparasitarios").descripcion("Protección contra pulgas, garrapatas y parásitos.").build()));
        cat.put("Alimento", categoriaRepository.save(Categoria.builder().nombre("Alimento").descripcion("Alimento balanceado y snacks para cada etapa de vida.").build()));
        cat.put("Higiene", categoriaRepository.save(Categoria.builder().nombre("Higiene").descripcion("Shampoo y productos de limpieza para tu mascota.").build()));
        cat.put("Accesorios", categoriaRepository.save(Categoria.builder().nombre("Accesorios").descripcion("Correas, transportadoras y accesorios para el día a día.").build()));

        List<Producto> prod = new ArrayList<>();
        prod.add(productoRepository.save(Producto.builder().nombre("Amoxibay 500mg").categoria(cat.get("Antibioticos")).especie("Perro / Gato").precio(4200).descuento(0).stock(45).stockCritico(8).descripcion("Antibiótico de amplio espectro en base a amoxicilina 500mg, caja de 30 cápsulas. Medicamento esencial de uso veterinario.").imagen("img/productos/p001.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Enrox 25mg").categoria(cat.get("Antibioticos")).especie("Perro / Gato").precio(6800).descuento(0).stock(30).stockCritico(6).descripcion("Antibiótico bactericida de amplio espectro en base a enrofloxacino 25mg, caja de tabletas. Indicado para infecciones bacterianas diversas.").imagen("img/productos/p002.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Metrotab vet. 250mg").categoria(cat.get("Antibioticos")).especie("Perro / Gato").precio(3900).descuento(0).stock(28).stockCritico(6).descripcion("Antiparasitario/antibiótico en base a metronidazol 250mg, comprimidos para perros y gatos (Virbac). Caja de 100 comprimidos. Uso frecuente en cuadros digestivos.").imagen("img/productos/p003.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("NexGard 68mg Comprimidos Masticables").categoria(cat.get("Antiparasitarios")).especie("Perro").precio(9500).descuento(15).stock(60).stockCritico(10).descripcion("Antiparasitario masticable en base a afoxolaner 68mg, para perros de 10,1-25kg. Mata pulgas, garrapatas y ácaros. Protege durante 30 días. Caja con 3 comprimidos.").imagen("img/productos/p004.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Bravecto 250mg Fluralaner").categoria(cat.get("Antiparasitarios")).especie("Perro").precio(18900).descuento(0).stock(40).stockCritico(8).descripcion("Antiparasitario masticable de acción prolongada (12 semanas de protección) en base a fluralaner 250mg, para perros de 4,5-10kg. Contra garrapatas, pulgas y ácaros de la sarna y del oído.").imagen("img/productos/p005.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Frontline Spray 250ml").categoria(cat.get("Antiparasitarios")).especie("Perro / Gato").precio(12500).descuento(0).stock(15).stockCritico(5).descripcion("Antiparasitario en spray en base a fipronil, acción inmediata contra pulgas y garrapatas. Apto para perros y gatos desde los 2 días de nacidos. Frasco de 250ml.").imagen("img/productos/p006.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Bravery Salmon Adulto Razas Grandes/Medianas 15kg").categoria(cat.get("Alimento")).especie("Perro").precio(39990).descuento(10).stock(20).stockCritico(4).descripcion("Alimento súper premium, grain free, 100% proteína animal de salmón, sin granos. Antioxidantes naturales, hipoalergénico. Para perros adultos de razas grandes y medianas.").imagen("img/productos/p007.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Fawna Patagonic Taste - Gato Esterilizado").categoria(cat.get("Alimento")).especie("Gato").precio(27990).descuento(0).stock(18).stockCritico(4).descripcion("Alimento completo y natural con salmón del Pacífico, 38% de proteínas, prebióticos y probióticos naturales. Formulado para gatos esterilizados, ayuda a eliminar bolas de pelo.").imagen("img/productos/p008.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Whimzees Daily Dental Treats (Toothbrush) 24un").categoria(cat.get("Alimento")).especie("Perro").precio(6990).descuento(0).stock(50).stockCritico(10).descripcion("Snacks dentales 100% naturales, sin granos ni gluten. Forma de cepillo que reduce placa y sarro, refresca el aliento. Para perros de 7-12kg, bolsa de 24 unidades.").imagen("img/productos/p009.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Skout's Honor Flea + Tick Shampoo 473ml").categoria(cat.get("Higiene")).especie("Perro / Gato").precio(8990).descuento(0).stock(25).stockCritico(5).descripcion("Shampoo antipulgas y garrapatas Max Strength, aroma menta y cedro. Elimina, repele y previene. Frasco de 16 fl. oz. (473ml).").imagen("img/productos/p010.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Correa Retráctil para Perro").categoria(cat.get("Accesorios")).especie("Perro").precio(7990).descuento(20).stock(12).stockCritico(3).descripcion("Correa retráctil resistente con mango ergonómico antideslizante, ideal para paseos diarios. Disponible en varios colores.").imagen("img/productos/p011.png").build()));
        prod.add(productoRepository.save(Producto.builder().nombre("Transportadora Grande para Viajes").categoria(cat.get("Accesorios")).especie("Perro / Gato").precio(24990).descuento(0).stock(5).stockCritico(5).descripcion("Transportadora rígida ventilada con puerta de rejilla metálica, apta para viajes en auto o avión y visitas a la clínica veterinaria.").imagen("img/productos/p012.png").build()));

        // ---------------- compras de demostración (para historial y reportes) ----------------
        // {cliente, díasAtrás, estado, producto1, cant1, producto2, cant2 ...}
        Object[][] compras = {
            {0, 1, EstadoOrden.PAGADA, 0, 2, 3, 1},
            {1, 2, EstadoOrden.ENVIADA, 6, 1, 8, 3},
            {2, 3, EstadoOrden.PAGADA, 4, 1},
            {3, 5, EstadoOrden.ENTREGADA, 9, 2, 10, 1},
            {4, 6, EstadoOrden.PAGADA, 7, 2},
            {0, 8, EstadoOrden.ENTREGADA, 1, 1, 5, 1},
            {1, 11, EstadoOrden.RECHAZADA, 11, 1},
            {2, 14, EstadoOrden.ENTREGADA, 3, 3, 0, 1},
            {3, 18, EstadoOrden.PAGADA, 6, 1},
            {4, 22, EstadoOrden.CANCELADA, 2, 1},
            {0, 27, EstadoOrden.ENTREGADA, 8, 2, 9, 4},
            {1, 35, EstadoOrden.ENTREGADA, 4, 2},
            {2, 48, EstadoOrden.ENTREGADA, 10, 2, 3, 2},
            {3, 62, EstadoOrden.ENTREGADA, 6, 1, 7, 1},
            {4, 90, EstadoOrden.ENTREGADA, 0, 3, 1, 2},
        };
        for (Object[] c : compras) {
            Usuario u = clientes.get((Integer) c[0]);
            Orden o = Orden.builder()
                    .usuario(u).estado((EstadoOrden) c[2]).total(0L)
                    .fecha(LocalDateTime.now().minusDays((Integer) c[1]).withHour(10 + (Integer) c[0] * 2).withMinute(15))
                    .clienteNombre(u.getNombre()).clienteApellidos(u.getApellido()).clienteCorreo(u.getCorreo())
                    .entregaCalle(u.getDireccion()).entregaDepartamento("").entregaRegion(u.getRegion())
                    .entregaComuna(u.getComuna()).entregaIndicaciones("").build();
            long total = 0;
            for (int i = 3; i < c.length; i += 2) {
                Producto p = prod.get((Integer) c[i]);
                int cantidad = (Integer) c[i + 1];
                int precio = p.precioFinal();
                long subtotal = (long) precio * cantidad;
                total += subtotal;
                o.agregarDetalle(DetalleOrden.builder().producto(p).nombreProducto(p.getNombre()).imagenProducto(p.getImagen())
                        .cantidad(cantidad).precioUnitario(precio).subtotal(subtotal).build());
            }
            o.setTotal(total);
            o = ordenRepository.save(o);
            o.setNumero(1000L + o.getId());
            ordenRepository.save(o);
        }
        log.info("Datos de prueba cargados: {} usuarios, {} categorías, {} productos, {} compras.",
                usuarioRepository.count(), categoriaRepository.count(), productoRepository.count(), ordenRepository.count());
    }

    private Usuario usuario(String nombre, String apellido, String nacimiento, String genero, String correo, String clave,
                            Rol rol, String direccion, String comuna) {
        return usuarioRepository.save(Usuario.builder()
                .nombre(nombre).apellido(apellido).fechaNacimiento(LocalDate.parse(nacimiento)).genero(genero)
                .correo(correo).password(passwordEncoder.encode(clave)).rol(rol).direccion(direccion)
                .region("Región Metropolitana de Santiago").comuna(comuna).build());
    }
}

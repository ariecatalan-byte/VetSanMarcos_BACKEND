package com.vetsanmarcos.service;

import com.vetsanmarcos.dto.*;
import com.vetsanmarcos.entity.*;
import com.vetsanmarcos.exception.ApiException;
import com.vetsanmarcos.repository.OrdenRepository;
import com.vetsanmarcos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    /**
     * Registra la compra del usuario autenticado. El servidor recalcula precios y totales desde la base de datos y
     * valida el stock con bloqueo de filas. Pago aprobado: descuenta stock; pago rechazado: queda registrado SIN tocar stock.
     */
    public OrdenResponse crear(OrdenRequest req, String correoActor) {
        Usuario usuario = usuarioService.actor(correoActor);

        // Une líneas repetidas del mismo producto
        Map<Long, Integer> pedidas = new LinkedHashMap<>();
        for (LineaOrdenRequest l : req.lineas()) {
            pedidas.merge(Long.valueOf(l.idProducto()), l.cantidad(), Integer::sum);
        }
        Map<Long, Producto> productos = new HashMap<>();
        for (Producto p : productoRepository.findAllByIdForUpdate(pedidas.keySet())) productos.put(p.getId(), p);

        Orden orden = Orden.builder()
                .usuario(usuario)
                .estado(req.pagoAprobado() ? EstadoOrden.PAGADA : EstadoOrden.RECHAZADA)
                .total(0L)
                .clienteNombre(req.cliente().nombre().trim()).clienteApellidos(req.cliente().apellidos().trim())
                .clienteCorreo(req.cliente().correo().trim())
                .entregaCalle(req.entrega().calle().trim())
                .entregaDepartamento(req.entrega().departamento() == null ? "" : req.entrega().departamento().trim())
                .entregaRegion(req.entrega().region()).entregaComuna(req.entrega().comuna())
                .entregaIndicaciones(req.entrega().indicaciones() == null ? "" : req.entrega().indicaciones().trim())
                .build();

        long total = 0;
        for (Map.Entry<Long, Integer> e : pedidas.entrySet()) {
            Producto p = productos.get(e.getKey());
            if (p == null || !Boolean.TRUE.equals(p.getActivo())) {
                throw ApiException.invalida("El producto " + e.getKey() + " no está disponible.");
            }
            int cantidad = e.getValue();
            if (req.pagoAprobado()) {
                if (p.getStock() < cantidad) {
                    throw ApiException.conflicto("No hay stock suficiente de \"" + p.getNombre() + "\". Disponible: " + p.getStock() + " unidad(es).");
                }
                p.setStock(p.getStock() - cantidad);
            }
            int precio = p.precioFinal();
            long subtotal = (long) precio * cantidad;
            total += subtotal;
            orden.agregarDetalle(DetalleOrden.builder().producto(p).nombreProducto(p.getNombre())
                    .imagenProducto(p.getImagen()).cantidad(cantidad).precioUnitario(precio).subtotal(subtotal).build());
        }
        orden.setTotal(total);
        orden = ordenRepository.save(orden);
        orden.setNumero(1000L + orden.getId());
        return Mapeador.aDto(ordenRepository.save(orden));
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> misOrdenes(String correoActor) {
        Usuario u = usuarioService.actor(correoActor);
        return ordenRepository.findByUsuarioIdOrderByIdDesc(u.getId()).stream().map(Mapeador::aDto).toList();
    }

    /** ADMIN: todas las órdenes, o las de un usuario concreto. */
    @Transactional(readOnly = true)
    public List<OrdenResponse> listar(Long usuarioId) {
        List<Orden> ordenes = usuarioId == null
                ? ordenRepository.findAllByOrderByIdDesc()
                : ordenRepository.findByUsuarioIdOrderByIdDesc(usuarioId);
        return ordenes.stream().map(Mapeador::aDto).toList();
    }

    /** Un usuario solo puede ver SUS órdenes; el administrador puede ver cualquiera. */
    @Transactional(readOnly = true)
    public OrdenResponse obtener(Long id, String correoActor) {
        Usuario actor = usuarioService.actor(correoActor);
        Orden o = ordenRepository.findWithDetallesById(id).orElseThrow(() -> ApiException.noEncontrado("La orden no existe."));
        if (actor.getRol() != Rol.ADMIN && !o.getUsuario().getId().equals(actor.getId())) {
            throw ApiException.prohibido("No puedes consultar información de otro usuario.");
        }
        return Mapeador.aDto(o);
    }

    public OrdenResponse cambiarEstado(Long id, String estadoTexto) {
        EstadoOrden nuevo = EstadoOrden.desdeTexto(estadoTexto);
        if (nuevo == null) throw ApiException.invalida("estado", "Estado inválido.");
        Orden o = ordenRepository.findWithDetallesById(id).orElseThrow(() -> ApiException.noEncontrado("La orden no existe."));
        if (!o.getEstado().puedePasarA(nuevo)) {
            throw ApiException.conflicto("No se puede cambiar una orden de " + o.getEstado().aFrontend() + " a " + nuevo.aFrontend() + ".");
        }
        if (nuevo == EstadoOrden.CANCELADA) { // devuelve el stock
            Set<Long> ids = new HashSet<>();
            o.getDetalles().forEach(d -> ids.add(d.getProducto().getId()));
            Map<Long, Producto> productos = new HashMap<>();
            productoRepository.findAllByIdForUpdate(ids).forEach(p -> productos.put(p.getId(), p));
            o.getDetalles().forEach(d -> {
                Producto p = productos.get(d.getProducto().getId());
                if (p != null) p.setStock(p.getStock() + d.getCantidad());
            });
        }
        o.setEstado(nuevo);
        return Mapeador.aDto(ordenRepository.save(o));
    }
}

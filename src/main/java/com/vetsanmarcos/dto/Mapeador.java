package com.vetsanmarcos.dto;

import com.vetsanmarcos.entity.*;

import java.time.format.DateTimeFormatter;

/** Convierte entidades a DTOs con la forma exacta que espera el frontend (ids como texto, roles en minúscula). */
public final class Mapeador {

    private Mapeador() {}

    private static String vacio(String s) { return s == null ? "" : s; }

    public static UsuarioResponse aDto(Usuario u) {
        return new UsuarioResponse(String.valueOf(u.getId()), u.getNombre(), u.getApellido(),
                u.getFechaNacimiento() == null ? "" : u.getFechaNacimiento().toString(), vacio(u.getGenero()),
                u.getCorreo(), vacio(u.getDireccion()), vacio(u.getRegion()), vacio(u.getComuna()),
                u.getRol().aFrontend(), Boolean.TRUE.equals(u.getBloqueado()), Boolean.TRUE.equals(u.getActivo()));
    }

    public static CategoriaResponse aDto(Categoria c) {
        return new CategoriaResponse(String.valueOf(c.getId()), c.getNombre(), vacio(c.getDescripcion()));
    }

    public static ProductoResponse aDto(Producto p) {
        return new ProductoResponse(String.valueOf(p.getId()), p.getNombre(), String.valueOf(p.getCategoria().getId()),
                vacio(p.getEspecie()), p.getPrecio(), p.getDescuento(), p.getStock(), p.getStockCritico(),
                vacio(p.getDescripcion()), vacio(p.getImagen()));
    }

    public static OrdenResponse aDto(Orden o) {
        return new OrdenResponse(String.valueOf(o.getId()), o.getNumero() == null ? 0 : o.getNumero(),
                o.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), o.getEstado().aFrontend(),
                String.valueOf(o.getUsuario().getId()),
                new ClienteDto(o.getClienteNombre(), o.getClienteApellidos(), o.getClienteCorreo()),
                new EntregaDto(o.getEntregaCalle(), vacio(o.getEntregaDepartamento()), o.getEntregaRegion(),
                        o.getEntregaComuna(), vacio(o.getEntregaIndicaciones())),
                o.getDetalles().stream().map(d -> new LineaOrdenResponse(String.valueOf(d.getProducto().getId()),
                        d.getNombreProducto(), vacio(d.getImagenProducto()), d.getPrecioUnitario(), d.getCantidad(),
                        d.getSubtotal())).toList(),
                o.getTotal());
    }
}

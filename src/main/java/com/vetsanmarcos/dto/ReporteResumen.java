package com.vetsanmarcos.dto;

import java.util.List;

/** Indicadores del dashboard administrativo, calculados con datos reales de la base de datos. */
public record ReporteResumen(
        String periodo,
        long totalOperaciones,
        long operacionesPagadas,
        long operacionesRechazadas,
        long operacionesCanceladas,
        long montoTotal,
        long unidadesVendidas,
        long ticketPromedio,
        List<TopProducto> topProductos,
        List<VentaCategoria> ventasPorCategoria,
        List<PuntoSerie> serie,
        Inventario inventario,
        long totalUsuarios) {

    public record TopProducto(String idProducto, String nombre, long unidades, long ingresos) {}
    public record VentaCategoria(String categoria, long monto) {}
    public record PuntoSerie(String etiqueta, long operaciones, long monto) {}
    public record Inventario(long totalProductos, long stockCritico, long enOferta) {}
}

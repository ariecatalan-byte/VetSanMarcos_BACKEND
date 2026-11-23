package com.vetsanmarcos.service;

import com.vetsanmarcos.dto.ReporteResumen;
import com.vetsanmarcos.dto.ReporteResumen.*;
import com.vetsanmarcos.entity.DetalleOrden;
import com.vetsanmarcos.entity.EstadoOrden;
import com.vetsanmarcos.entity.Orden;
import com.vetsanmarcos.exception.ApiException;
import com.vetsanmarcos.repository.OrdenRepository;
import com.vetsanmarcos.repository.ProductoRepository;
import com.vetsanmarcos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    /** periodo: semana (7 días), mes (30 días), anio (365 días) o todo. */
    public ReporteResumen resumen(String periodoTexto) {
        String periodo = periodoTexto == null ? "mes" : periodoTexto.trim().toLowerCase();
        LocalDate hoy = LocalDate.now();
        LocalDate desdeDia = switch (periodo) {
            case "semana" -> hoy.minusDays(6);
            case "mes" -> hoy.minusDays(29);
            case "anio" -> hoy.minusDays(364);
            case "todo" -> LocalDate.of(2000, 1, 1);
            default -> throw ApiException.invalida("periodo", "El periodo debe ser semana, mes, anio o todo.");
        };
        List<Orden> ordenes = ordenRepository.findConDetallesDesde(desdeDia.atStartOfDay());

        long pagadas = 0, rechazadas = 0, canceladas = 0, monto = 0, unidades = 0;
        Map<String, long[]> porProducto = new HashMap<>();      // id -> {unidades, ingresos}
        Map<String, String> nombres = new HashMap<>();
        Map<String, Long> porCategoria = new HashMap<>();
        boolean porDia = periodo.equals("semana") || periodo.equals("mes");
        Map<String, long[]> serie = new TreeMap<>();            // clave ordenable -> {operaciones, monto}
        if (porDia) {
            for (LocalDate d = desdeDia; !d.isAfter(hoy); d = d.plusDays(1)) serie.put(d.toString(), new long[2]);
        } else if (periodo.equals("anio")) {
            YearMonth m = YearMonth.from(hoy).minusMonths(11);
            for (int i = 0; i < 12; i++) serie.put(m.plusMonths(i).toString(), new long[2]);
        }

        for (Orden o : ordenes) {
            if (o.getEstado() == EstadoOrden.RECHAZADA) rechazadas++;
            else if (o.getEstado() == EstadoOrden.CANCELADA) canceladas++;
            if (!EstadoOrden.VENTAS.contains(o.getEstado())) continue;

            pagadas++;
            monto += o.getTotal();
            String clave = porDia ? o.getFecha().toLocalDate().toString() : YearMonth.from(o.getFecha()).toString();
            long[] punto = serie.computeIfAbsent(clave, k -> new long[2]);
            punto[0]++;
            punto[1] += o.getTotal();

            for (DetalleOrden d : o.getDetalles()) {
                unidades += d.getCantidad();
                String id = String.valueOf(d.getProducto().getId());
                nombres.put(id, d.getNombreProducto());
                long[] acc = porProducto.computeIfAbsent(id, k -> new long[2]);
                acc[0] += d.getCantidad();
                acc[1] += d.getSubtotal();
                String cat = d.getProducto().getCategoria() == null ? "Sin categoría" : d.getProducto().getCategoria().getNombre();
                porCategoria.merge(cat, d.getSubtotal(), Long::sum);
            }
        }

        List<TopProducto> top = porProducto.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
                .limit(5)
                .map(e -> new TopProducto(e.getKey(), nombres.get(e.getKey()), e.getValue()[0], e.getValue()[1])).toList();
        List<VentaCategoria> cats = porCategoria.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> new VentaCategoria(e.getKey(), e.getValue())).toList();
        DateTimeFormatter dia = DateTimeFormatter.ofPattern("dd/MM");
        List<PuntoSerie> puntos = serie.entrySet().stream()
                .map(e -> new PuntoSerie(porDia ? LocalDate.parse(e.getKey()).format(dia) : e.getKey(), e.getValue()[0], e.getValue()[1])).toList();

        var productos = productoRepository.findByActivoTrueOrderByIdAsc();
        Inventario inventario = new Inventario(productos.size(),
                productos.stream().filter(p -> p.getStock() <= p.getStockCritico()).count(),
                productos.stream().filter(p -> p.getDescuento() > 0).count());

        return new ReporteResumen(periodo, ordenes.size(), pagadas, rechazadas, canceladas, monto, unidades,
                pagadas == 0 ? 0 : Math.round((double) monto / pagadas), top, cats, puntos, inventario, usuarioRepository.count());
    }
}

package com.vetsanmarcos.entity;

import java.util.EnumSet;
import java.util.Set;

/** Estados de una orden: PAGADA → ENVIADA → ENTREGADA; PAGADA/ENVIADA → CANCELADA; RECHAZADA es terminal. */
public enum EstadoOrden {
    PAGADA, ENVIADA, ENTREGADA, CANCELADA, RECHAZADA;

    /** Estados que cuentan como venta real en los reportes. */
    public static final Set<EstadoOrden> VENTAS = EnumSet.of(PAGADA, ENVIADA, ENTREGADA);

    public String aFrontend() {
        return name().toLowerCase();
    }

    public static EstadoOrden desdeTexto(String valor) {
        if (valor == null) return null;
        try {
            return valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean puedePasarA(EstadoOrden destino) {
        return switch (this) {
            case PAGADA -> destino == ENVIADA || destino == CANCELADA;
            case ENVIADA -> destino == ENTREGADA || destino == CANCELADA;
            default -> false;
        };
    }
}

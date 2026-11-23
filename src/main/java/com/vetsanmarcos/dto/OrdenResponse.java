package com.vetsanmarcos.dto;

import java.util.List;

public record OrdenResponse(String id, long numero, String fecha, String estado, String usuarioId, ClienteDto cliente,
                            EntregaDto entrega, List<LineaOrdenResponse> lineas, long total) {}

package com.vetsanmarcos.controller;

import com.vetsanmarcos.dto.ReporteResumen;
import com.vetsanmarcos.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reportes", description = "Indicadores del dashboard administrativo")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resumen de ventas e inventario. periodo = semana | mes | anio | todo (ADMIN)")
    public ReporteResumen resumen(@RequestParam(defaultValue = "mes") String periodo) {
        return reporteService.resumen(periodo);
    }
}

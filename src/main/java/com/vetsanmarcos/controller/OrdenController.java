package com.vetsanmarcos.controller;

import com.vetsanmarcos.dto.CambioEstadoRequest;
import com.vetsanmarcos.dto.OrdenRequest;
import com.vetsanmarcos.dto.OrdenResponse;
import com.vetsanmarcos.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Órdenes / Compras", description = "Compras del cliente, historial y gestión de estados")
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Confirmar una compra del usuario autenticado (precios y stock se validan en el servidor)")
    public OrdenResponse crear(@Valid @RequestBody OrdenRequest req, Authentication auth) {
        return ordenService.crear(req, auth.getName());
    }

    @GetMapping("/mis")
    @Operation(summary = "Historial del usuario autenticado (solo sus operaciones)")
    public List<OrdenResponse> mias(Authentication auth) {
        return ordenService.misOrdenes(auth.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Todas las órdenes; filtro opcional ?usuarioId= (ADMIN)")
    public List<OrdenResponse> listar(@RequestParam(required = false) Long usuarioId) {
        return ordenService.listar(usuarioId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una orden (solo su dueño o ADMIN; si no, 403)")
    public OrdenResponse obtener(@PathVariable Long id, Authentication auth) {
        return ordenService.obtener(id, auth.getName());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar estado: pagada→enviada→entregada, o cancelada (devuelve stock) (ADMIN)")
    public OrdenResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest req) {
        return ordenService.cambiarEstado(id, req.estado());
    }
}

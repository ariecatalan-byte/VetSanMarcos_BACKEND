package com.vetsanmarcos.controller;

import com.vetsanmarcos.dto.ProductoRequest;
import com.vetsanmarcos.dto.ProductoResponse;
import com.vetsanmarcos.dto.StockRequest;
import com.vetsanmarcos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Catálogo (público) con filtros opcionales: categoriaId, busqueda, precioMax")
    public List<ProductoResponse> listar(@RequestParam(required = false) Long categoriaId,
                                         @RequestParam(required = false) String busqueda,
                                         @RequestParam(required = false) Integer precioMax) {
        return productoService.listar(categoriaId, busqueda, precioMax);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de producto (público)")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear producto (ADMIN)")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest req) {
        return productoService.crear(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto (ADMIN)")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return productoService.actualizar(id, req);
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar solo el stock (ADMIN y VENDEDOR)")
    public ProductoResponse cambiarStock(@PathVariable Long id, @Valid @RequestBody StockRequest req) {
        return productoService.cambiarStock(id, req.stock());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto; si ya fue vendido se desactiva (ADMIN)")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}

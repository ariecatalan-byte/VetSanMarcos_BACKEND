package com.vetsanmarcos.service;

import com.vetsanmarcos.dto.Mapeador;
import com.vetsanmarcos.dto.ProductoRequest;
import com.vetsanmarcos.dto.ProductoResponse;
import com.vetsanmarcos.entity.Categoria;
import com.vetsanmarcos.entity.Producto;
import com.vetsanmarcos.exception.ApiException;
import com.vetsanmarcos.repository.CategoriaRepository;
import com.vetsanmarcos.repository.DetalleOrdenRepository;
import com.vetsanmarcos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetalleOrdenRepository detalleRepository;

    /** Catálogo público (solo activos) con filtros opcionales combinables: categoría, precio final máximo y texto. */
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(Long categoriaId, String busqueda, Integer precioMax) {
        String texto = busqueda == null ? "" : busqueda.trim().toLowerCase();
        return productoRepository.findByActivoTrueOrderByIdAsc().stream()
                .filter(p -> categoriaId == null || p.getCategoria().getId().equals(categoriaId))
                .filter(p -> precioMax == null || p.precioFinal() <= precioMax)
                .filter(p -> texto.isEmpty() || p.getNombre().toLowerCase().contains(texto)
                        || (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(texto)))
                .map(Mapeador::aDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        Producto p = productoRepository.findById(id).filter(Producto::getActivo)
                .orElseThrow(() -> ApiException.noEncontrado("El producto no existe."));
        return Mapeador.aDto(p);
    }

    public ProductoResponse crear(ProductoRequest req) {
        Producto p = Producto.builder().build();
        aplicar(p, req);
        return Mapeador.aDto(productoRepository.save(p));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        Producto p = productoRepository.findById(id).orElseThrow(() -> ApiException.noEncontrado("El producto no existe."));
        aplicar(p, req);
        return Mapeador.aDto(productoRepository.save(p));
    }

    /** ADMIN y VENDEDOR pueden modificar SOLO el stock. */
    public ProductoResponse cambiarStock(Long id, int stock) {
        Producto p = productoRepository.findById(id).filter(Producto::getActivo)
                .orElseThrow(() -> ApiException.noEncontrado("El producto no existe."));
        p.setStock(stock);
        return Mapeador.aDto(productoRepository.save(p));
    }

    /** Si el producto ya figura en alguna boleta se DESACTIVA (el historial no se rompe); si no, se elimina. */
    public void eliminar(Long id) {
        Producto p = productoRepository.findById(id).orElseThrow(() -> ApiException.noEncontrado("El producto no existe."));
        if (detalleRepository.existsByProductoId(id)) {
            p.setActivo(false);
            productoRepository.save(p);
        } else {
            productoRepository.delete(p);
        }
    }

    private void aplicar(Producto p, ProductoRequest req) {
        Categoria c = categoriaRepository.findById(Long.valueOf(req.categoriaId()))
                .orElseThrow(() -> ApiException.invalida("categoriaId", "La categoría seleccionada no existe."));
        p.setNombre(req.nombre().trim());
        p.setCategoria(c);
        p.setEspecie(req.especie().trim());
        p.setPrecio(req.precio());
        p.setDescuento(req.descuento());
        p.setStock(req.stock());
        p.setStockCritico(req.stockCritico());
        p.setDescripcion(req.descripcion().trim());
        p.setImagen(req.imagen() == null ? "" : req.imagen().trim());
        p.setActivo(true);
    }
}

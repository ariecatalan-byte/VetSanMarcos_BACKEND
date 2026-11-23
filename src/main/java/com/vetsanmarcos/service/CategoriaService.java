package com.vetsanmarcos.service;

import com.vetsanmarcos.dto.CategoriaRequest;
import com.vetsanmarcos.dto.CategoriaResponse;
import com.vetsanmarcos.dto.Mapeador;
import com.vetsanmarcos.entity.Categoria;
import com.vetsanmarcos.exception.ApiException;
import com.vetsanmarcos.repository.CategoriaRepository;
import com.vetsanmarcos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAllByOrderByIdAsc().stream().map(Mapeador::aDto).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return Mapeador.aDto(buscar(id));
    }

    public CategoriaResponse crear(CategoriaRequest req) {
        String nombre = req.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw ApiException.conflicto("Ya existe una categoría con ese nombre.");
        }
        return Mapeador.aDto(categoriaRepository.save(Categoria.builder()
                .nombre(nombre).descripcion(req.descripcion() == null ? "" : req.descripcion().trim()).build()));
    }

    public CategoriaResponse actualizar(Long id, CategoriaRequest req) {
        Categoria c = buscar(id);
        String nombre = req.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw ApiException.conflicto("Ya existe una categoría con ese nombre.");
        }
        c.setNombre(nombre);
        c.setDescripcion(req.descripcion() == null ? "" : req.descripcion().trim());
        return Mapeador.aDto(categoriaRepository.save(c));
    }

    /** Coherencia de datos: no se elimina una categoría que todavía tiene productos. */
    public void eliminar(Long id) {
        Categoria c = buscar(id);
        long enUso = productoRepository.countByCategoriaId(id);
        if (enUso > 0) {
            throw ApiException.conflicto("No se puede eliminar: hay " + enUso + " producto(s) en esta categoría. Reasígnalos o elimínalos primero.");
        }
        categoriaRepository.delete(c);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> ApiException.noEncontrado("La categoría no existe."));
    }
}

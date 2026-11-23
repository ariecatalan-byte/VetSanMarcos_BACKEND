package com.vetsanmarcos.repository;

import com.vetsanmarcos.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("""
        SELECT p FROM Producto p
        WHERE p.activo = true
          AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
          AND (:busqueda IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')))
          AND (:precioMax IS NULL OR p.precio <= :precioMax)
        """)
    Page<Producto> buscar(
            @Param("categoriaId") Long categoriaId,
            @Param("busqueda") String busqueda,
            @Param("precioMax") java.math.BigDecimal precioMax,
            Pageable pageable);
}

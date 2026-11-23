package com.vetsanmarcos.repository;

import com.vetsanmarcos.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrueOrderByIdAsc();

    long countByCategoriaId(Long categoriaId);

    /** Bloquea las filas para que dos compras simultáneas no vendan el mismo stock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id in :ids")
    List<Producto> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);
}

package com.vetsanmarcos.repository;

import com.vetsanmarcos.entity.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    boolean existsByProductoId(Long productoId);
}

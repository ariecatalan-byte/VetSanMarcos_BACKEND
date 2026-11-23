package com.vetsanmarcos.repository;

import com.vetsanmarcos.entity.Orden;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    @EntityGraph(attributePaths = {"usuario", "detalles"})
    List<Orden> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"usuario", "detalles"})
    List<Orden> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    @EntityGraph(attributePaths = {"usuario", "detalles"})
    Optional<Orden> findWithDetallesById(Long id);

    boolean existsByUsuarioId(Long usuarioId);

    @Query("select distinct o from Orden o left join fetch o.detalles where o.fecha >= :desde")
    List<Orden> findConDetallesDesde(@Param("desde") LocalDateTime desde);
}

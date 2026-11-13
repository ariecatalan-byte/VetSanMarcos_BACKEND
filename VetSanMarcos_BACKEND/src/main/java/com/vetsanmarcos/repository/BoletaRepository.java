package com.vetsanmarcos.repository;

import com.vetsanmarcos.entity.Boleta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    Page<Boleta> findByUsuarioId(Long usuarioId, Pageable pageable);
    List<Boleta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
    boolean existsByNumero(String numero);
}

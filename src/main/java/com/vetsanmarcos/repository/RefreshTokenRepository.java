package com.vetsanmarcos.repository;

import com.vetsanmarcos.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("select t from RefreshToken t join fetch t.usuario where t.tokenHash = :hash")
    Optional<RefreshToken> findByTokenHash(@Param("hash") String hash);

    @Modifying
    @Query("update RefreshToken t set t.revocado = true where t.usuario.id = :usuarioId and t.revocado = false")
    int revocarTodosDe(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("delete from RefreshToken t where t.usuario.id = :usuarioId")
    int eliminarTodosDe(@Param("usuarioId") Long usuarioId);
}

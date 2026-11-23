package com.vetsanmarcos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/** Refresh token guardado como HASH SHA-256 (si se filtra la BD no sirve para renovar sesiones). */
@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Instant expiraEn;

    @Column(nullable = false)
    @Builder.Default
    private Boolean revocado = false;

    @Column(nullable = false)
    private Instant creadoEn;
}

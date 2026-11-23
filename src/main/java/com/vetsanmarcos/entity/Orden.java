package com.vetsanmarcos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Compra / boleta de un usuario autenticado. */
@Entity
@Table(name = "ordenes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número visible de boleta (1000 + id). Se asigna al guardar, así no hay carreras entre compras simultáneas. */
    @Column(unique = true)
    private Long numero;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Column(nullable = false)
    private Long total;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Datos del cliente en la compra
    @Column(nullable = false, length = 50) private String clienteNombre;
    @Column(nullable = false, length = 100) private String clienteApellidos;
    @Column(nullable = false, length = 100) private String clienteCorreo;

    // Dirección de entrega
    @Column(nullable = false, length = 200) private String entregaCalle;
    @Column(length = 50) private String entregaDepartamento;
    @Column(nullable = false, length = 100) private String entregaRegion;
    @Column(nullable = false, length = 100) private String entregaComuna;
    @Column(length = 300) private String entregaIndicaciones;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<DetalleOrden> detalles = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
    }

    public void agregarDetalle(DetalleOrden d) {
        d.setOrden(this);
        detalles.add(d);
    }
}

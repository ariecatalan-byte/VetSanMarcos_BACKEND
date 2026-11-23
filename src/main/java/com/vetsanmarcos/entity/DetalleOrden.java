package com.vetsanmarcos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_orden")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /** Copia del nombre e imagen al momento de la compra (la boleta no cambia si luego editan el producto). */
    @Column(nullable = false, length = 100)
    private String nombreProducto;

    @Column(length = 255)
    private String imagenProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer precioUnitario;

    @Column(nullable = false)
    private Long subtotal;
}

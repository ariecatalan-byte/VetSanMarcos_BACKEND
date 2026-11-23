package com.vetsanmarcos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 60)
    private String especie;

    /** Precio normal en CLP (entero). */
    @Column(nullable = false)
    private Integer precio;

    /** Porcentaje 0-90. Mayor a 0 = oferta. */
    @Column(nullable = false)
    @Builder.Default
    private Integer descuento = 0;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockCritico = 5;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 255)
    private String imagen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /** false = producto desactivado (eliminación lógica cuando ya fue vendido). */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    public int precioFinal() {
        int d = descuento == null ? 0 : descuento;
        return (int) Math.round(precio * (100 - d) / 100.0);
    }
}

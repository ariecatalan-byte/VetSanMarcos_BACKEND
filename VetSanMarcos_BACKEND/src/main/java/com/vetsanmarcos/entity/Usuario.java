package com.vetsanmarcos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    private LocalDate fechaNacimiento;

    private String telefono;

    private String direccion;

    private String region;

    private String comuna;

    @Column(nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean bloqueado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (intentosFallidos == null) intentosFallidos = 0;
        if (bloqueado == null) bloqueado = false;
        if (activo == null) activo = true;
    }
}

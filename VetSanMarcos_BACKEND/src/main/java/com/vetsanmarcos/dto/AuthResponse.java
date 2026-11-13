package com.vetsanmarcos.dto;

import com.vetsanmarcos.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
}

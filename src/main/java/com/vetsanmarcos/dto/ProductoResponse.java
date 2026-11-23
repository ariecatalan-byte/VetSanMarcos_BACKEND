package com.vetsanmarcos.dto;

public record ProductoResponse(String id, String nombre, String categoriaId, String especie, int precio, int descuento,
                               int stock, int stockCritico, String descripcion, String imagen) {}

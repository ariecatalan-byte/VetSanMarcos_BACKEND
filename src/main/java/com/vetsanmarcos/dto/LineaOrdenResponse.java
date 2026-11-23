package com.vetsanmarcos.dto;

public record LineaOrdenResponse(String idProducto, String nombre, String imagen, int precioUnitario, int cantidad, long subtotal) {}

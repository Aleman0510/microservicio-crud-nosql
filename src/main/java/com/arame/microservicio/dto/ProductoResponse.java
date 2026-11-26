package com.arame.microservicio.dto;

public record ProductoResponse(
        String id,
        String nombre,
        String descripcion,
        Double precio,
        Integer stock
) {
}

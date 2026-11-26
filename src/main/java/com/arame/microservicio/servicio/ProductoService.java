package com.arame.microservicio.servicio;

import com.arame.microservicio.dto.ProductoRequest;
import com.arame.microservicio.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {

    ProductoResponse crear(ProductoRequest request);

    List<ProductoResponse> listar();

    ProductoResponse obtenerPorId(String id);

    ProductoResponse actualizar(String id, ProductoRequest request);

    void eliminar(String id);
}

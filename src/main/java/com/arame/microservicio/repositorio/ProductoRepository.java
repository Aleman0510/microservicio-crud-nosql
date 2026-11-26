package com.arame.microservicio.repositorio;

import com.arame.microservicio.modelo.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    Optional<Producto> findByNombreIgnoreCase(String nombre);
}

package com.arame.microservicio.servicio;

import com.arame.microservicio.dto.ProductoRequest;
import com.arame.microservicio.dto.ProductoResponse;
import com.arame.microservicio.excepcion.DatoDuplicadoException;
import com.arame.microservicio.excepcion.RecursoNoEncontradoException;
import com.arame.microservicio.modelo.Producto;
import com.arame.microservicio.repositorio.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        productoRepository.findByNombreIgnoreCase(request.nombre())
                .ifPresent(producto -> {
                    throw new DatoDuplicadoException("Ya existe un producto con el nombre: " + request.nombre());
                });

        Producto producto = convertir(request);
        return convertir(productoRepository.save(producto));
    }

    @Override
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream()
                .map(this::convertir)
                .toList();
    }

    @Override
    public ProductoResponse obtenerPorId(String id) {
        Producto producto = buscarProducto(id);
        return convertir(producto);
    }

    @Override
    public ProductoResponse actualizar(String id, ProductoRequest request) {
        Producto producto = buscarProducto(id);

        productoRepository.findByNombreIgnoreCase(request.nombre())
                .filter(duplicado -> !duplicado.getId().equals(id))
                .ifPresent(duplicado -> {
                    throw new DatoDuplicadoException("Ya existe un producto con el nombre: " + request.nombre());
                });

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());

        return convertir(productoRepository.save(producto));
    }

    @Override
    public void eliminar(String id) {
        buscarProducto(id);
        productoRepository.deleteById(id);
    }

    private Producto buscarProducto(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    private Producto convertir(ProductoRequest request) {
        return Producto.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stock(request.stock())
                .build();
    }

    private ProductoResponse convertir(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}

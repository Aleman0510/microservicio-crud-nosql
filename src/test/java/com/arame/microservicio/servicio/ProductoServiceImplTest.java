package com.arame.microservicio.servicio;

import com.arame.microservicio.dto.ProductoRequest;
import com.arame.microservicio.dto.ProductoResponse;
import com.arame.microservicio.excepcion.DatoDuplicadoException;
import com.arame.microservicio.excepcion.RecursoNoEncontradoException;
import com.arame.microservicio.modelo.Producto;
import com.arame.microservicio.repositorio.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private ProductoRequest request;
    private Producto producto;

    @BeforeEach
    void configurar() {
        request = new ProductoRequest("Teclado", "Teclado mecánico", 800.0, 10);
        producto = Producto.builder()
                .id("abc123")
                .nombre("Teclado")
                .descripcion("Teclado mecánico")
                .precio(800.0)
                .stock(10)
                .build();
    }

    @Test
    void crearDebeGuardarProducto() {
        when(productoRepository.findByNombreIgnoreCase("Teclado")).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponse response = productoService.crear(request);

        assertEquals("abc123", response.id());
        assertEquals("Teclado", response.nombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void crearDebeRechazarNombreDuplicado() {
        when(productoRepository.findByNombreIgnoreCase("Teclado")).thenReturn(Optional.of(producto));

        assertThrows(DatoDuplicadoException.class, () -> productoService.crear(request));
    }

    @Test
    void listarDebeRetornarProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<ProductoResponse> productos = productoService.listar();

        assertEquals(1, productos.size());
        assertEquals("Teclado", productos.get(0).nombre());
    }

    @Test
    void obtenerDebeRetornarProducto() {
        when(productoRepository.findById("abc123")).thenReturn(Optional.of(producto));

        ProductoResponse response = productoService.obtenerPorId("abc123");

        assertEquals("abc123", response.id());
    }

    @Test
    void obtenerDebeLanzarExcepcionCuandoNoExiste() {
        when(productoRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.obtenerPorId("999"));
    }

    @Test
    void actualizarDebeModificarProducto() {
        ProductoRequest actualizado = new ProductoRequest("Laptop", "Laptop escolar", 12000.0, 5);
        when(productoRepository.findById("abc123")).thenReturn(Optional.of(producto));
        when(productoRepository.findByNombreIgnoreCase("Laptop")).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponse response = productoService.actualizar("abc123", actualizado);

        assertEquals("Laptop", response.nombre());
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizarDebeRechazarNombreDuplicado() {
        Producto otro = Producto.builder().id("otro").nombre("Laptop").build();
        ProductoRequest actualizado = new ProductoRequest("Laptop", "Laptop escolar", 12000.0, 5);
        when(productoRepository.findById("abc123")).thenReturn(Optional.of(producto));
        when(productoRepository.findByNombreIgnoreCase("Laptop")).thenReturn(Optional.of(otro));

        assertThrows(DatoDuplicadoException.class,
                () -> productoService.actualizar("abc123", actualizado));
    }

    @Test
    void eliminarDebeEliminarProducto() {
        when(productoRepository.findById("abc123")).thenReturn(Optional.of(producto));

        productoService.eliminar("abc123");

        verify(productoRepository).deleteById("abc123");
    }

    @Test
    void eliminarDebeLanzarExcepcionCuandoNoExiste() {
        when(productoRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.eliminar("999"));
    }
}

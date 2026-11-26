package com.arame.microservicio.controlador;

import com.arame.microservicio.dto.ProductoRequest;
import com.arame.microservicio.dto.ProductoResponse;
import com.arame.microservicio.excepcion.RecursoNoEncontradoException;
import com.arame.microservicio.servicio.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void crearDebeResponder201() throws Exception {
        ProductoResponse response = new ProductoResponse("abc123", "Teclado", "Teclado mecánico", 800.0, 10);
        when(productoService.crear(any(ProductoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nombre": "Teclado",
                                  "descripcion": "Teclado mecánico",
                                  "precio": 800,
                                  "stock": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void crearDebeResponder400CuandoLosDatosSonInvalidos() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nombre": "",
                                  "descripcion": "",
                                  "precio": -1,
                                  "stock": -2
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarDebeResponder200() throws Exception {
        ProductoResponse producto = new ProductoResponse("abc123", "Teclado", "Teclado mecánico", 800.0, 10);
        when(productoService.listar()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Teclado"));
    }

    @Test
    void obtenerDebeResponder200() throws Exception {
        ProductoResponse producto = new ProductoResponse("abc123", "Teclado", "Teclado mecánico", 800.0, 10);
        when(productoService.obtenerPorId("abc123")).thenReturn(producto);

        mockMvc.perform(get("/api/v1/productos/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void obtenerDebeResponder404() throws Exception {
        when(productoService.obtenerPorId("999"))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado"));

        mockMvc.perform(get("/api/v1/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarDebeResponder200() throws Exception {
        ProductoResponse producto = new ProductoResponse("abc123", "Laptop", "Laptop escolar", 12000.0, 5);
        when(productoService.actualizar(eq("abc123"), any(ProductoRequest.class))).thenReturn(producto);

        mockMvc.perform(put("/api/v1/productos/abc123")
                        .contentType("application/json")
                        .content("""
                                {
                                  "nombre": "Laptop",
                                  "descripcion": "Laptop escolar",
                                  "precio": 12000,
                                  "stock": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    void eliminarDebeResponder204() throws Exception {
        doNothing().when(productoService).eliminar("abc123");

        mockMvc.perform(delete("/api/v1/productos/abc123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarDebeResponder404() throws Exception {
        doThrow(new RecursoNoEncontradoException("Producto no encontrado"))
                .when(productoService).eliminar("999");

        mockMvc.perform(delete("/api/v1/productos/999"))
                .andExpect(status().isNotFound());
    }
}

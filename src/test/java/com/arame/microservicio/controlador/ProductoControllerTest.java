package com.arame.microservicio.controlador;

import com.arame.microservicio.dto.ProductoRequest;
import com.arame.microservicio.dto.ProductoResponse;
import com.arame.microservicio.servicio.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk());
    }
}

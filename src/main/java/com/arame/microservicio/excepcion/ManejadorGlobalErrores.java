package com.arame.microservicio.excepcion;

import com.arame.microservicio.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalErrores {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManejadorGlobalErrores.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(
            RecursoNoEncontradoException exception,
            HttpServletRequest request) {
        return crearError(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarDuplicado(
            DatoDuplicadoException exception,
            HttpServletRequest request) {
        return crearError(HttpStatus.CONFLICT, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        return crearError(HttpStatus.BAD_REQUEST, "Error de validación", request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        return crearError(HttpStatus.BAD_REQUEST, "El cuerpo JSON no tiene un formato válido", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(
            Exception exception,
            HttpServletRequest request) {
        LOGGER.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), exception);
        return crearError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor", request, null);
    }

    private ResponseEntity<ErrorResponse> crearError(
            HttpStatus status,
            String mensaje,
            HttpServletRequest request,
            Map<String, String> erroresValidacion) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                erroresValidacion
        );
        return ResponseEntity.status(status).body(response);
    }
}

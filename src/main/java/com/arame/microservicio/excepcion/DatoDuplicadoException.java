package com.arame.microservicio.excepcion;

public class DatoDuplicadoException extends RuntimeException {

    public DatoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}

package com.crediya.excepciones;

/**
 * Excepción lanzada cuando se detecta un intento de registrar un pago duplicado
 * (mismo préstamo, misma fecha y mismo monto).
 */
public class PagoDuplicadoException extends CrediYaException {

    public PagoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public PagoDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

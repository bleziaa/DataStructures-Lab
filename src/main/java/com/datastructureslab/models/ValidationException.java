package com.datastructureslab.models;

/** Error de validación de entrada del usuario, con un mensaje claro en español. */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

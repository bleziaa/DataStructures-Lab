package com.datastructureslab.models;

/** Validación de textos escritos por el usuario. Nunca devuelve basura: o un número válido o una ValidationException. */
public final class InputValidator {
    public static final int MIN_VALUE = -9999;
    public static final int MAX_VALUE = 9999;

    private InputValidator() {}

    public static int parseInt(String raw, String field) {
        String text = raw == null ? "" : raw.trim();
        if (text.isEmpty()) {
            throw new ValidationException("El campo «" + field + "» está vacío.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new ValidationException("«" + field + "» debe ser un número entero (sin letras ni decimales).");
        }
    }

    /** Valor que se guarda en una estructura. */
    public static int parseValue(String raw, String field) {
        int v = parseInt(raw, field);
        if (v < MIN_VALUE || v > MAX_VALUE) {
            throw new ValidationException("«" + field + "» debe estar entre " + MIN_VALUE + " y " + MAX_VALUE + ".");
        }
        return v;
    }

    /** Cantidad de elementos, filas o columnas: de 1 a max. */
    public static int parseSize(String raw, String field, int max) {
        int v = parseInt(raw, field);
        if (v < 0) {
            throw new ValidationException("«" + field + "» no puede ser negativo.");
        }
        if (v == 0) {
            throw new ValidationException("«" + field + "» no puede ser cero; debe ser al menos 1.");
        }
        if (v > max) {
            throw new ValidationException("«" + field + "» no puede ser mayor que " + max + " en esta visualización.");
        }
        return v;
    }

    /** Índice, fila o columna: entero no negativo (la existencia la comprueba la estructura). */
    public static int parseIndex(String raw, String field) {
        int v = parseInt(raw, field);
        if (v < 0) {
            throw new ValidationException("«" + field + "» no puede ser negativo.");
        }
        return v;
    }
}

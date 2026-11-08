package com.datastructureslab.structures;

/** Se lanza al intentar leer o eliminar de una estructura vacía. */
public class EmptyStructureException extends RuntimeException {
    public EmptyStructureException(String message) {
        super(message);
    }
}

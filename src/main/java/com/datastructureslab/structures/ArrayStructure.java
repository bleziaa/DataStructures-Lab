package com.datastructureslab.structures;

import java.util.ArrayList;
import java.util.List;

/** Arreglo de tamaño fijo. Las posiciones sin valor se representan con null. */
public class ArrayStructure {
    private final Integer[] data;

    public ArrayStructure(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño del arreglo debe ser mayor que cero.");
        }
        this.data = new Integer[size];
    }

    public int length() {
        return data.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= data.length) {
            throw new IndexOutOfBoundsException(
                    "El índice " + index + " no existe. Los índices válidos van de 0 a " + (data.length - 1) + ".");
        }
    }

    /** Guarda o modifica el valor de una posición. Devuelve el valor anterior (o null). */
    public Integer set(int index, int value) {
        checkIndex(index);
        Integer previous = data[index];
        data[index] = value;
        return previous;
    }

    /** Devuelve el valor de la posición, o null si todavía no fue registrado. */
    public Integer get(int index) {
        checkIndex(index);
        return data[index];
    }

    public int filledCount() {
        int count = 0;
        for (Integer v : data) {
            if (v != null) count++;
        }
        return count;
    }

    /** Primer índice donde está el valor, o -1 si no existe. */
    public int indexOf(int value) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null && data[i] == value) return i;
        }
        return -1;
    }

    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        for (Integer v : data) list.add(v);
        return list;
    }

    public void clear() {
        java.util.Arrays.fill(data, null);
    }
}

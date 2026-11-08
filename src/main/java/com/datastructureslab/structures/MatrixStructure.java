package com.datastructureslab.structures;

/** Matriz de filas x columnas. Las celdas sin valor se representan con null. */
public class MatrixStructure {
    private final Integer[][] data;
    private final int rows;
    private final int cols;

    public MatrixStructure(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Las filas y las columnas deben ser mayores que cero.");
        }
        this.rows = rows;
        this.cols = cols;
        this.data = new Integer[rows][cols];
    }

    public int rows() { return rows; }

    public int cols() { return cols; }

    public int totalElements() { return rows * cols; }

    private void check(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("La posición (" + row + ", " + col
                    + ") no existe. Filas válidas: 0 a " + (rows - 1)
                    + ", columnas válidas: 0 a " + (cols - 1) + ".");
        }
    }

    public Integer set(int row, int col, int value) {
        check(row, col);
        Integer previous = data[row][col];
        data[row][col] = value;
        return previous;
    }

    public Integer get(int row, int col) {
        check(row, col);
        return data[row][col];
    }

    public int filledCount() {
        int count = 0;
        for (Integer[] r : data) for (Integer v : r) if (v != null) count++;
        return count;
    }

    public void clear() {
        for (Integer[] r : data) java.util.Arrays.fill(r, null);
    }
}

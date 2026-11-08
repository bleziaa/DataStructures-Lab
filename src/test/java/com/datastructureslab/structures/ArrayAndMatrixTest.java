package com.datastructureslab.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ArrayAndMatrixTest {

    @Test
    void arrayRejectsInvalidSizes() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayStructure(0));
        assertThrows(IllegalArgumentException.class, () -> new ArrayStructure(-3));
    }

    @Test
    void arraySetGetAndModify() {
        ArrayStructure a = new ArrayStructure(3);
        assertNull(a.get(1));
        assertNull(a.set(1, 40));
        assertEquals(40, a.get(1));
        assertEquals(40, a.set(1, 55));
        assertEquals(55, a.get(1));
        assertEquals(1, a.filledCount());
        assertEquals(1, a.indexOf(55));
        assertEquals(-1, a.indexOf(99));
        assertEquals(Arrays.asList(null, 55, null), a.toList());
    }

    @Test
    void arrayRejectsMissingIndexes() {
        ArrayStructure a = new ArrayStructure(2);
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> a.set(-1, 5));
    }

    @Test
    void arrayClearAndSingleElement() {
        ArrayStructure a = new ArrayStructure(1);
        a.set(0, 7);
        a.clear();
        assertNull(a.get(0));
        assertEquals(0, a.filledCount());
    }

    @Test
    void matrixRejectsInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new MatrixStructure(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new MatrixStructure(3, -1));
    }

    @Test
    void matrixSetGetAndTotals() {
        MatrixStructure m = new MatrixStructure(2, 3);
        assertEquals(6, m.totalElements());
        assertNull(m.get(1, 2));
        m.set(1, 2, 9);
        assertEquals(9, m.get(1, 2));
        assertEquals(9, m.set(1, 2, 10));
        assertEquals(1, m.filledCount());
        m.clear();
        assertEquals(0, m.filledCount());
    }

    @Test
    void matrixRejectsMissingPositions() {
        MatrixStructure m = new MatrixStructure(2, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> m.get(2, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> m.set(0, -1, 1));
    }
}

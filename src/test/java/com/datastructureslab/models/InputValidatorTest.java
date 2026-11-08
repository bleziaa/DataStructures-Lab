package com.datastructureslab.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InputValidatorTest {

    @Test
    void emptyAndNonNumeric() {
        assertThrows(ValidationException.class, () -> InputValidator.parseInt("", "valor"));
        assertThrows(ValidationException.class, () -> InputValidator.parseInt("   ", "valor"));
        assertThrows(ValidationException.class, () -> InputValidator.parseInt(null, "valor"));
        assertThrows(ValidationException.class, () -> InputValidator.parseInt("abc", "valor"));
        assertThrows(ValidationException.class, () -> InputValidator.parseInt("3.5", "valor"));
        assertThrows(ValidationException.class, () -> InputValidator.parseInt("99999999999", "valor"));
        assertEquals(12, InputValidator.parseInt(" 12 ", "valor"));
    }

    @Test
    void sizesRejectZeroNegativeAndTooBig() {
        assertThrows(ValidationException.class, () -> InputValidator.parseSize("0", "filas", 8));
        assertThrows(ValidationException.class, () -> InputValidator.parseSize("-2", "filas", 8));
        assertThrows(ValidationException.class, () -> InputValidator.parseSize("9", "filas", 8));
        assertEquals(8, InputValidator.parseSize("8", "filas", 8));
    }

    @Test
    void valuesAndIndexes() {
        assertThrows(ValidationException.class, () -> InputValidator.parseValue("10000", "valor"));
        assertThrows(ValidationException.class, () -> InputValidator.parseValue("-10000", "valor"));
        assertEquals(-9999, InputValidator.parseValue("-9999", "valor"));
        assertThrows(ValidationException.class, () -> InputValidator.parseIndex("-1", "índice"));
        assertEquals(0, InputValidator.parseIndex("0", "índice"));
    }
}

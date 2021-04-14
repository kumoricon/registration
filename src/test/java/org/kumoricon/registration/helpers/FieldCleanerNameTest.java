package org.kumoricon.registration.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FieldCleanerNameTest {
    @ParameterizedTest
    @CsvSource(value = {"Alice:Alice",
                        "alice:Alice",
                        "  Alice  :Alice",
                        "McGregor:McGregor",
                        "mcGregor:McGregor",
                        " alice jones :Alice Jones"}, delimiter = ':')
    public void cleanName(String input, String expected) {
        assertEquals(expected, FieldCleaner.cleanName(input));
    }

    @Test
    public void cleanNameNull() {
        assertNull(FieldCleaner.cleanName(null));
    }
}
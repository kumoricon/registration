package org.kumoricon.registration.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FieldCleanerPhoneNumberTest {
    @ParameterizedTest
    @CsvSource(value = {"(123) 123-1234:(123) 123-1234",
            " (123) 123-1234 :(123) 123-1234",
            "1231231234:(123) 123-1234",
            "123-1234:123-1234",
            "1231234:1231234",
            "1231231234 x104:1231231234 x104"}, delimiter = ':')
    public void cleanPhoneNumber(String input, String expected) {
        assertEquals(expected, FieldCleaner.cleanPhoneNumber(input));
    }

    @Test
    public void cleanPhoneNumberNull() {
        assertNull(FieldCleaner.cleanPhoneNumber(null));
    }
}
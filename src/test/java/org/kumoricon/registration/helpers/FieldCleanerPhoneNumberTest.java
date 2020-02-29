package org.kumoricon.registration.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class FieldCleanerPhoneNumberTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"(123) 123-1234", "(123) 123-1234"},
                {" (123) 123-1234 ", "(123) 123-1234"},
                {"1231231234", "(123) 123-1234"},
                {"123-1234", "123-1234"},
                {"1231234", "1231234"},
                {"1231231234 x104", "1231231234 x104"},
                {null, null}
        });
    }

    private String input;
    private String expected;

    public FieldCleanerPhoneNumberTest(String input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void cleanPhoneNumber() {
        assertEquals(expected, FieldCleaner.cleanPhoneNumber(input));
    }
}
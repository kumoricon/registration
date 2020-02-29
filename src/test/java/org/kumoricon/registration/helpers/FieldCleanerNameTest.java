package org.kumoricon.registration.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FieldCleanerNameTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"Alice", "Alice"},
                {"alice", "Alice"},
                {"  Alice  ", "Alice"},
                {"McGregor", "McGregor"},
                {"mcGregor", "McGregor"},
                {" alice jones ", "Alice Jones"},
                {null, null}
        });
    }

    private String input;
    private String expected;

    public FieldCleanerNameTest(String input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void cleanName() {
        assertEquals(expected, FieldCleaner.cleanName(input));
    }
}
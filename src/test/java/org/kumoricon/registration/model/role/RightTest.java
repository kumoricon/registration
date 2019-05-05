package org.kumoricon.registration.model.role;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RightTest {
    private Right r1 = new Right(1, "test", "first right");
    private Right r2 = new Right(2, "test", "second right");
    private Right r3 = new Right(3, "other", "third right");


    @Test
    public void equalsTests() {
        // Rights with the same name should be equal. Names are enforced as unique in the database.
        // This makes it easier to check that rights exist.
        // In this case, ID is just the id used in the database for foreign keys
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
    }

    @Test
    public void hashCodeTests() {
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }
}
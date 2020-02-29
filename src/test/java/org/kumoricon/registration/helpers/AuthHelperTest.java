package org.kumoricon.registration.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kumoricon.registration.model.role.Right;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class AuthHelperTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "do_things", true },
                { "do_stuff", true },
                { "DO_THINGS", false }, // Comparison is case sensitive, by convention rights should be lower case
                { "not_this", false },
                { null, true }          // If the right is null, assume the user has that right (because there are
                                        // no special requirements for that action
        });
    }

    private String right;
    private Boolean expected;

    public AuthHelperTest(String right, Boolean expected) {
        this.right = right;
        this.expected = expected;
    }

    @Test
    public void userHasAuthority() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null,
                        Set.of(new Right("do_things"),
                                new Right("do_stuff")));

        assertEquals(expected, AuthHelper.userHasAuthority(auth, right));
    }
}
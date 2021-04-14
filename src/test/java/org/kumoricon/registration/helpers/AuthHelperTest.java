package org.kumoricon.registration.helpers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.kumoricon.registration.model.role.Right;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.annotation.HttpMethodConstraint;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AuthHelperTest {
    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("do_things", true),
                Arguments.of("do_stuff", true),
                Arguments.of("DO_THINGS", false), // Comparison is case sensitive, by convention rights should be lower case
                Arguments.of("not_this", false),
                Arguments.of(null, true)          // If the right is null, assume the user has that right (because there are
                                                   // no special requirements for that action
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void userHasAuthority(String right, Boolean expected) {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null,
                        Set.of(new Right("do_things"),
                                new Right("do_stuff")));

        assertEquals(expected, AuthHelper.userHasAuthority(auth, right));
    }
}
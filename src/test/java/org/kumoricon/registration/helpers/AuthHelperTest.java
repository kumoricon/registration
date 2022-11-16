package org.kumoricon.registration.helpers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.kumoricon.registration.model.role.Right;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AuthHelperTest {
    @ParameterizedTest
    @ArgumentsSource(MyArgumentsProvider.class)
    public void userHasAuthority(String right, Boolean expected) {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null,
                        Set.of(new Right("do_things"),
                                new Right("do_stuff")));

        assertEquals(expected, AuthHelper.userHasAuthority(auth, right));
    }


    public static class MyArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of("do_things", true),
                Arguments.of("do_stuff", true),
                Arguments.of("DO_THINGS", false), // Comparison is case sensitive, by convention rights should be lower case
                Arguments.of("not_this", false),
                Arguments.of(null, true)          // If the right is null, assume the user has that right (because there are
                                                            // no special requirements for that action
            );
        }
    }
}

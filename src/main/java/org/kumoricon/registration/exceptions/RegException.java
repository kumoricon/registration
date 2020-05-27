package org.kumoricon.registration.exceptions;

/**
 * Base class for Registration Exceptions
 */
public class RegException extends RuntimeException {
    public RegException(String message) {
        super(message);
    }
}

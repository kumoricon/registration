package org.kumoricon.registration.exceptions;

/**
 * Thrown when a database record is not found.
 *
 * Note that this should only be used for things like searching by id. For example, if a row for id=5
 * does not exist .findById(5) should fail and throw this exception.
 *
 * But searching for a list that could be empty should not. For example, .findAll()
 * `select * from attendees;` should succeed but return an empty list.
 *
 * Make sure to set an appropriate error message in the exception, since that will be exposed to
 * end users.
 */
public class NotFoundException extends RegException {
    public NotFoundException(String message) {
        super(message);
    }
}

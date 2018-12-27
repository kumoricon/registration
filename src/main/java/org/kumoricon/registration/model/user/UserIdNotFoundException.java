package org.kumoricon.registration.model.user;


public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(Integer id) {
        super("User id " + id + " not found");
    }
}

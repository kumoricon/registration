package org.kumoricon.registration.inlinereg;


public class InLineRegistrationException extends Exception {
    public InLineRegistrationException(String errorMessage) {
        super(errorMessage);
    }

    public InLineRegistrationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}

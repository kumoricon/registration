package org.kumoricon.registration.model.attendee;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Service
public class AttendeeValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Attendee.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // This doesn't validate nearly as much as you'd think because Specialty coordinators (or anyone with the
        // right permissions) can create Attendees with no first name and lastname, no birthdate, etc.

        Attendee attendee = (Attendee) target;

        if (attendee.getPaidAmount() != null && attendee.getPaidAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue("paidAmount", "notnegative.paidAmount", "Paid Amount must not be negative");
        }

        if (attendee.getName().isEmpty() && attendee.getFanName().isEmpty()) {
            errors.reject("notnull.name", "Name or Fan Name is required");
        }
    }
}

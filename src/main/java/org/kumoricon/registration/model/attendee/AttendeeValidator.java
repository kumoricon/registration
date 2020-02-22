package org.kumoricon.registration.model.attendee;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;

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

        if (attendee.getName().trim().isEmpty() && attendee.getFanName().trim().isEmpty()) {
            errors.reject("notnull.name", "Name or Fan Name is required");
        }

        if (attendee.getBadgeId() == null) {
            errors.reject("notnull.badgeId", "Badge type is required");
        }
    }

    /**
     * Additional validations that may not be required, depending on the right that the user has.
     * @param attendee Attendee to validate
     * @param errors Errors
     */
    public void validateForRegularUser(Attendee attendee, Errors errors) {
        if (isNullOrEmpty(attendee.getFirstName())) {
            errors.reject("notnull.firstName", "First Name is Required");
        }
        if (isNullOrEmpty(attendee.getLastName())) {
            errors.reject("notnull.lastName", "Last name is required");
        }

        if (attendee.getBirthDate() == null) {
            errors.reject("notnull.birthDate", "Birth Date is required");
        }

        if (attendee.getBirthDate().isBefore(LocalDate.of(1900, 1, 1))) {
            errors.reject("baddate.birthDate", "Birth date must be 1/1/1900 or later");
        }
        if (attendee.getBirthDate().isAfter(LocalDate.now())) {
            errors.reject("baddate.birthDate", "Birth date may not be in the future");
        }

        // Don't require phone number or email for child badges (<13)
        if (attendee.getAge() >= 13 && isNullOrEmpty(attendee.getEmail()) && isNullOrEmpty(attendee.getPhoneNumber())) {
            errors.reject("notnull.phoneOrEmail", "Phone or Email is required");
        }

        if (isNullOrEmpty(attendee.getZip())) {
            errors.reject("notnull.zip", "Zip code is required");
        }

        if (isNullOrEmpty(attendee.getEmergencyContactFullName())) {
            errors.reject("notnull.emergencyContact", "Emergency contact is required");
        }

        if (isNullOrEmpty(attendee.getEmergencyContactPhone())) {
            errors.reject("notnull.emergencyContact", "Emergency contact is required");
        }

        if (attendee.isMinor()) {
            if (isNullOrEmpty(attendee.getParentFullName())) {
                errors.reject("notnull.parentFullName", "Parent name is required");
            }
            if (isNullOrEmpty(attendee.getParentPhone())) {
                errors.reject("notnull.parentPhone", "Parent phone number is required");
            }
            if (attendee.getParentFormReceived() == null || !attendee.getParentFormReceived()) {
                errors.reject("notnull.parentForm", "Parental consent form is required");
            }
        }

    }


    private static boolean isNullOrEmpty(String data) {
        return data == null || data.trim().isEmpty();
    }

}

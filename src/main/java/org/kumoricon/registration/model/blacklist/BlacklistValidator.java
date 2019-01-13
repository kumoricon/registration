package org.kumoricon.registration.model.blacklist;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
public class BlacklistValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return BlacklistName.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "firstName", "required.firstname","Field First Name is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "lastName", "required.lastname","Field Last Name is required.");
    }
}

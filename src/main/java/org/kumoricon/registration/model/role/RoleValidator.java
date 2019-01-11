package org.kumoricon.registration.model.role;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
public class RoleValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Role.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "name", "required.name","Field Name is required.");
    }
}

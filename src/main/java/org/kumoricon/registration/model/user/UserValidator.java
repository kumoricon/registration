package org.kumoricon.registration.model.user;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "username", "required.username","Field Username is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "roleId", "required.role", "Field Role is required");
    }
}

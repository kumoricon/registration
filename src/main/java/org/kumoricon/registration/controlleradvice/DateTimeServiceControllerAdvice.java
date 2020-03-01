package org.kumoricon.registration.controlleradvice;

import org.kumoricon.registration.helpers.DateTimeService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


/**
 * Adds the DateTimeService to every model so it can be used to format dates in Thymeleaf templates.
 */
@ControllerAdvice
public class DateTimeServiceControllerAdvice {
    private final DateTimeService dateTimeService;

    public DateTimeServiceControllerAdvice(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @ModelAttribute("dts")
    public DateTimeService getService() {
        return dateTimeService;
    }
}

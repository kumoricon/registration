package org.kumoricon.registration;

import org.kumoricon.registration.helpers.DateTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    private final DateTimeService dateTimeService;

    public ErrorController(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(final Throwable throwable, final Model model, final HttpServletRequest request) {
        if (AccessDeniedException.class == throwable.getClass()) {
            logger.error("on {} calling [{}] got exception: {}",
                    request.getRemoteAddr(),
                    request.getRequestURI(),
                    throwable.getMessage());
        } else {
            logger.error("{} calling [{}] got exception: {}",
                    request.getRemoteAddr(),
                    request.getRequestURI(),
                    throwable.getMessage(),
                    throwable);

        }

        model.addAttribute("err", throwable.getMessage());
        model.addAttribute("timestamp", dateTimeService.epochToDateString(System.currentTimeMillis()));
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("exception", throwable);
        return "error";
    }

}


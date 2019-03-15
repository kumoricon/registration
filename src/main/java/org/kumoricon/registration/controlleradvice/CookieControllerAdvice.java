package org.kumoricon.registration.controlleradvice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * This class gets information from various cookies and injects it in to the Thymeleaf model
 * class, so they can be used directly in all templates.
 */
@ControllerAdvice
public class CookieControllerAdvice {
    public static final String PRINTER_COOKIE_NAME = "PRINTERNAME";
    public static final String TILL_COOKIE_NAME = "TILLNAME";

    @ModelAttribute("selectedPrinter")
    public String selectedPrinter(final HttpServletRequest request) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(PRINTER_COOKIE_NAME)) {
                return c.getValue();
            }
        }
        return null;
    }

    /**
     * Provides the till number, or null if empty. Till is typically just a number
     * @param request
     * @return
     */
    @ModelAttribute("selectedTill")
    public String selectedTill(final HttpServletRequest request) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(TILL_COOKIE_NAME)) {
                return c.getValue();
            }
        }
        return null;
    }

}
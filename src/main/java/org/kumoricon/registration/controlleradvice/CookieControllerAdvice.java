package org.kumoricon.registration.controlleradvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(CookieControllerAdvice.class);
    /**
     * Provides the currently selected printer name, or null if the cookie is missing or malformed.
     * @param request Current HTTP request
     * @return Value from the cookie PRINTERNAME {@link #PRINTER_COOKIE_NAME}
     */
    @ModelAttribute("selectedPrinter")
    public String selectedPrinter(final HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(PRINTER_COOKIE_NAME)) {
                    // This has a try/catch around it so that the UI isn't broken if any weird values
                    // end up in the cookie and it fails to parse.
                    try {
                        PrinterSettings settings = PrinterSettings.fromCookieValue(c.getValue());
                        return settings.getPrinterName();
                    } catch (Exception ex) {
                        log.warn("Invalid printer settings in cookie {}: {}", PRINTER_COOKIE_NAME, c.getValue());
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Provides the till name, or null if empty. Till is typically just a number
     * @param request Current HTTP request
     * @return Value from cookie TILLNAME {@link #TILL_COOKIE_NAME}
     */
    @ModelAttribute("selectedTill")
    public String selectedTill(final HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals(TILL_COOKIE_NAME)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

}
package org.kumoricon.registration.utility;


import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class PrinterController {
    private final PrinterService printerService;
    private static final Logger log = LoggerFactory.getLogger(PrinterController.class);

    public PrinterController(PrinterService printerService) {
        this.printerService = printerService;
    }

    @RequestMapping(value = "/utility/printer")
    public String printer(Model model,
                          @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerName) {
        model.addAttribute("printer", printerName);
        model.addAttribute("availablePrinters", printerService.getPrinterNames());
        return "utility/printer";
    }

    @RequestMapping(value = "/utility/printer", method = RequestMethod.POST)
    public String setPrinter(Model model,
                             @RequestParam String printer,
                             @RequestParam String action,
                             HttpServletResponse response) {
        String newPrinter = printer.trim();
        String urlSafePrinterName = getUrlSafePrinterName(newPrinter);

        if (action.equals("Test")) {
            log.info("Printed test badge to " + newPrinter);
            // TODO: send test badge to selected printer
            return "redirect:/utility/printer?msg=Printed+Test+Badge+to+" + urlSafePrinterName;
        } else if (action.equals("Save") || action.equals("Select")) {
            log.info("Setting printer to {}", newPrinter);
            try {
                model.addAttribute("printer", newPrinter);
                Cookie cookie = new Cookie(CookieControllerAdvice.PRINTER_COOKIE_NAME, newPrinter);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            } catch (Exception e) {
                log.error("Error setting printer to {}", newPrinter, e);
                model.addAttribute("err", e.getMessage());
                return "utilty/printer";
            }

            return "redirect:/utility/printer?msg=Selected+printer+" + urlSafePrinterName;

        }

        throw new RuntimeException("Missing 'action' field in form");
    }


    private String getUrlSafePrinterName(String printer) {
        try {
            return URLEncoder.encode(printer, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("Error converting to URL safe printer name", e);
            throw new RuntimeException(e);
        }
    }
}
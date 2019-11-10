package org.kumoricon.registration.utility;


import org.apache.tomcat.util.codec.binary.Base64;
import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.print.BadgePrintService;
import org.kumoricon.registration.print.PrinterInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.PrintException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Controller
public class PrinterController {
    private final PrinterInfoService printerInfoService;
    private final BadgePrintService badgePrintService;
    private static final Logger log = LoggerFactory.getLogger(PrinterController.class);

    public PrinterController(PrinterInfoService printerInfoService, BadgePrintService badgePrintService) {
        this.printerInfoService = printerInfoService;
        this.badgePrintService = badgePrintService;
    }

    @RequestMapping(value = "/utility/printer")
    public String printer(HttpServletRequest request,
                          Model model,
                          @RequestParam(value="previousUrl", required = false) String previousUrl,
                          @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        PrinterSettings settings = PrinterSettings.fromCookieValue(printerCookie);
        String previousLink;

        if (previousUrl == null || previousUrl.isBlank()) {
            previousLink = request.getHeader("referer");
            previousUrl = Base64.encodeBase64URLSafeString(previousLink.getBytes());
        } else {
            previousLink = new String(Base64.decodeBase64(previousUrl));
        }
        model.addAttribute("printer", settings.getPrinterName());
        model.addAttribute("xOffset", settings.getxOffset());
        model.addAttribute("yOffset", settings.getyOffset());
        model.addAttribute("availablePrinters", printerInfoService.getPrinterNames());
        model.addAttribute("previousUrl", previousUrl);
        model.addAttribute("previousLink", previousLink);
        return "utility/printer";
    }

    @RequestMapping(value = "/utility/printer", method = RequestMethod.POST)
    public String setPrinter(Model model,
                             @RequestParam String printer,
                             @RequestParam(required = false, defaultValue = "0") Integer xOffset,
                             @RequestParam(required = false, defaultValue = "0") Integer yOffset,
                             @RequestParam(required = false) String previousUrl,
                             @RequestParam String action,
                             HttpServletResponse response) {
        String newPrinter = printer.trim();
        String urlSafePrinterName = getUrlSafePrinterName(newPrinter);
        previousUrl = sanitizePreviousUrl(previousUrl);
        String previousLink = new String(Base64.decodeBase64(previousUrl));
        PrinterSettings settings = new PrinterSettings(newPrinter, xOffset, yOffset);

        model.addAttribute("previousUrl", previousUrl);
        model.addAttribute("previousLink", previousLink);

        if (action.equals("Test")) {
            log.info("Printed test badge to " + newPrinter);
            model.addAttribute("printer", printer);
            model.addAttribute("xOffset", xOffset);
            model.addAttribute("yOffset", yOffset);
            model.addAttribute("availablePrinters", printerInfoService.getPrinterNames());
            try {
                badgePrintService.printTestBadge(settings);
                model.addAttribute("msg", "Printed test badge to " + newPrinter);
                return "utility/printer";
            } catch (PrintException ex) {
                log.error("Error printing to {}", newPrinter, ex);
                model.addAttribute("err", ex.getMessage());
                return "utility/printer";
            }
        } else if (action.equals("Save") || action.equals("Select")) {
            log.info("Setting printer to {}", settings);
            try {
                model.addAttribute("printer", newPrinter);
                Cookie cookie = new Cookie(CookieControllerAdvice.PRINTER_COOKIE_NAME, settings.asCookieValue());
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            } catch (Exception e) {
                log.error("Error setting printer to {}", newPrinter, e);
                model.addAttribute("err", e.getMessage());
                return "utility/printer";
            }

            return "redirect:/utility/printer?previousUrl="+previousUrl+"&msg=Selected+printer+" + urlSafePrinterName;

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

    private String sanitizePreviousUrl(String previousUrl) {
        // If an array of values was passed in, just use the first. Seems to happen with some forms,
        // I'm guessing because they have an <input type="hidden"> with the same name in two different forms.
        // But my (possibly wrong) understanding is that separate forms should be submitted, well, separately.
        // If that's not true, rework the forms in printer.html and clean this up
        if (previousUrl.contains(",")) {
            return previousUrl.split(",")[0];
        }
        return previousUrl;
    }
}
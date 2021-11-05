package org.kumoricon.registration.utility;


import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;

@Controller
public class PrinterController {
    private final PrinterInfoService printerInfoService;
    private final BadgePrintService badgePrintService;
    private static final Logger log = LoggerFactory.getLogger(PrinterController.class);
    private final Base64.Decoder decoder = Base64.getUrlDecoder();
    private final Base64.Encoder encoder = Base64.getUrlEncoder();

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
            previousUrl = encoder.encodeToString(previousLink.getBytes());
        } else {
            previousLink = new String(decoder.decode(previousUrl));
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
        String previousLink = new String(decoder.decode(previousUrl));
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

                // Build the Set-Cookie header manually because not all the options are supported
                // by the Java cookie class
                StringBuilder cookie = new StringBuilder(CookieControllerAdvice.PRINTER_COOKIE_NAME);
                cookie.append("=");
                cookie.append(settings.asCookieValue());
                cookie.append("; ");
                DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.US);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, 1);
                cookie.append("Expires=" + df.format(cal.getTime()) + "; ");
                cookie.append("Path=/; SameSite=Strict; HttpOnly; ");
                response.setHeader("Set-Cookie", cookie.toString());
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
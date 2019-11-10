package org.kumoricon.registration.guest;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.print.BadgePrintService;
import org.kumoricon.registration.print.Sides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.PrintException;
import java.util.List;

@Controller
public class GuestPrintController {
    private final GuestRepository guestRepository;
    private final BadgePrintService badgePrintService;

    private static final Logger log = LoggerFactory.getLogger(GuestPrintController.class);

    @Autowired
    public GuestPrintController(BadgePrintService badgePrintService, GuestRepository guestRepository) {
        this.badgePrintService = badgePrintService;
        this.guestRepository = guestRepository;
    }

    @RequestMapping(value = "/guests/{guestId}/print", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public String printGuestBadge(@PathVariable Integer guestId,
                               @RequestParam(value = "sides", defaultValue = "both") String sides,
                               @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie,
                               @AuthenticationPrincipal User user) {

        Guest guest = guestRepository.findById(guestId);

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        Sides printSides = Sides.from(sides);
        log.info("printing badge side {} for {} on {}", printSides, guest, printerSettings.getPrinterName());

        try {
            String result = badgePrintService.printBadgesForGuest(List.of(guest), printSides, printerSettings);
            return "redirect:/guests/" + guest.getId() + "?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:/guests/" + guest.getId() + "?err=" + ex.getMessage();
        }
    }
}

package org.kumoricon.registration.order;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.print.BadgePrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.print.PrintException;
import java.util.List;

@Controller
public class ReprintController {
    private final AttendeeRepository attendeeRepository;
    private final BadgePrintService badgePrintService;
    private final AttendeeHistoryRepository attendeeHistoryRepository;

    private static final Logger log = LoggerFactory.getLogger(ReprintController.class);

    public ReprintController(BadgePrintService badgePrintService, AttendeeRepository attendeeRepository, AttendeeHistoryRepository attendeeHistoryRepository) {
        this.badgePrintService = badgePrintService;
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/reprint", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('reprint_badge')")
    public String reprintBadge(@PathVariable Integer orderId,
                               @PathVariable Integer attendeeId,
                               @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie,
                               @AuthenticationPrincipal User user) {
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        log.info("reprinting badge for {} with {}", attendee, printerSettings);

        try {
            String result = badgePrintService.printBadgesForAttendees(List.of(attendee), printerSettings);
            AttendeeHistory ah = new AttendeeHistory(user, attendee.getId(), "Reprinted Badge");
            attendeeHistoryRepository.save(ah);
            return "redirect:/orders/" + attendee.getOrderId() + "/attendees/" + attendee.getId() + "?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:/orders/" + attendee.getOrderId() + "/attendees/" + attendee.getId() + "?err=" + ex.getMessage();
        }
    }
}

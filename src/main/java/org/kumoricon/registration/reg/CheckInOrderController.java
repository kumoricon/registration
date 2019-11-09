package org.kumoricon.registration.reg;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.print.BadgePrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.PrintException;
import java.util.ArrayList;
import java.util.List;


/**
 * This controller handles the pre-reg checkin flow for an entire order
 */
@Controller
public class CheckInOrderController {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final AttendeeService attendeeService;
    private final BadgePrintService badgePrintService;

    private static final Logger log = LoggerFactory.getLogger(CheckInOrderController.class);

    @Autowired
    public CheckInOrderController(AttendeeRepository attendeeRepository,
                                  AttendeeHistoryRepository attendeeHistoryRepository,
                                  AttendeeService attendeeService,
                                  BadgePrintService badgePrintService) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.attendeeService = attendeeService;
        this.badgePrintService = badgePrintService;
    }

    @RequestMapping(value = "/reg/checkinorder/{orderId}", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('pre_reg_check_in_order')")
    public String checkInOrder(Model model,
                               @PathVariable Integer orderId) {
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);
        boolean orderCheckedIn = true;
        int numberOfMinors = 0;
        for (Attendee attendee : attendees) {
            if (!attendee.getCheckedIn()) {
                orderCheckedIn = false;
            }
            if (attendee.isMinor()) {
                numberOfMinors++;
            }
        }
        List<AttendeeHistoryDTO> notes = attendeeHistoryRepository.findAllDTObyOrderId(orderId);
        model.addAttribute("attendees", attendees);
        model.addAttribute("notes", notes);
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderCheckedIn", orderCheckedIn);
        model.addAttribute("numberOfMinors", numberOfMinors);
        return "reg/checkinorder-id";
    }


    @RequestMapping(value = "/reg/checkinorder/{orderId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in_order')")
    public String checkInOrder(@PathVariable Integer orderId,
                          @RequestParam(value = "chkParentForm", required = false) Boolean parentFormReceived,
                          @AuthenticationPrincipal User user,
                          @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);
        List<Attendee> output = new ArrayList<>();
        for (Attendee attendee : attendees) {
            if (!attendee.getCheckedIn()) {
                output.add(attendeeService.checkInAttendee(attendee.getId(), user, attendee.isMinor()));
            }
        }

        log.info("printed badge(s) for {} when checking in order {}", output, orderId);

        try {
            String result = badgePrintService.printBadgesForAttendees(output, printerSettings);
            return "redirect:/reg/checkinorder/" + orderId + "?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:/reg/checkinorder/" + orderId + "?err=" + ex.getMessage();
        }
    }

    @RequestMapping(value = "/reg/checkinorder/{orderId}/reprint/{attendeeId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in_order')")
    public String checkInOrder(@PathVariable Integer orderId,
                               @PathVariable Integer attendeeId,
                               @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);

        log.info("reprinted badge during order check in for {}", attendee);

        if (!attendee.getCheckedIn()) {
            return "redirect:/reg/checkinorder/" + orderId + "?err=Not+checked+in";
        }
        try {
            String result = badgePrintService.printBadgesForAttendees(List.of(attendee), printerSettings);
            return "redirect:/reg/checkinorder/" + orderId + "?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:/reg/checkinorder/" + orderId + "?err=" + ex.getMessage();
        }
    }
}

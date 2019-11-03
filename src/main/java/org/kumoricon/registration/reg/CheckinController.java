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
 * This controller handles the pre-reg checkin flow, verifying attendee information and
 * printing a badge
 */
@Controller
public class CheckinController {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final AttendeeService attendeeService;
    private final BadgePrintService badgePrintService;

    private static final Logger log = LoggerFactory.getLogger(CheckinController.class);

    @Autowired
    public CheckinController(AttendeeRepository attendeeRepository,
                             AttendeeHistoryRepository attendeeHistoryRepository,
                             AttendeeService attendeeService,
                             BadgePrintService badgePrintService) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.attendeeService = attendeeService;
        this.badgePrintService = badgePrintService;
    }

    @RequestMapping(value = "/reg/checkin/{id}")
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String verifyData(Model model,
                         @PathVariable Integer id) {
        try {
            Attendee attendee = attendeeRepository.findById(id);
            model.addAttribute("attendee", attendee);
            model.addAttribute("history", attendeeHistoryRepository.findAllDTObyAttendeeId(id));
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        return "reg/checkin-id";
    }

    @RequestMapping(value = "/reg/checkin/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String checkIn(@PathVariable Integer id,
                          @RequestParam(value = "chkParentForm", required = false) Boolean parentFormReceived,
                          @AuthenticationPrincipal User user,
                          @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {

        Attendee attendee = attendeeService.checkInAttendee(id, user, parentFormReceived);

        if (attendee.isBadgePrePrinted()) {
            return "redirect:/reg/checkin/" + attendee.getId() + "/printbadge?err=Badge+is+pre-+printed";
        }
        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        try {
            String result = badgePrintService.printBadgesForAttendees(List.of(attendee), printerSettings);
            return "redirect:/reg/checkin/" + attendee.getId() + "/printbadge?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:/reg/checkin/" + attendee.getId() + "/printbadge?err=" + ex.getMessage();
        }
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

        try {
            String result = badgePrintService.printBadgesForAttendees(output, printerSettings);
            return "redirect:/reg/checkinorder/" + orderId + "?msg=" + result;
        } catch (PrintException ex) {
            log.error("Error printing", ex);
            return "redirect:/reg/checkinorder/" + orderId + "?err=" + ex.getMessage();
        }
    }


    @RequestMapping(value = "/reg/checkin/{id}/printbadge")
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String printBadge(Model model,
                             @PathVariable Integer id) {
        Attendee attendee = attendeeRepository.findById(id);
        if (!attendee.getCheckedIn()) {
            return "redirect:/reg/checkin/" + attendee.getId() + "?err=attendee+not+checked+in";
        }

        model.addAttribute("attendee", attendee);
        return "reg/checkin-id-printbadge";
    }

    @RequestMapping(value = "/reg/checkin/{id}/printbadge", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String printBadgeAction(@PathVariable Integer id,
                                   @RequestParam(required=false , value = "action") String action,
                                   @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        Attendee attendee = attendeeRepository.findById(id);
        PrinterSettings settings = PrinterSettings.fromCookieValue(printerCookie);

        if (action != null && action.equals("badgePrintedSuccessfully") && attendee != null) {
            log.info("reports badge printed successfully for {}", attendee);
            attendee.setBadgePrinted(true);
            attendeeRepository.save(attendee);
            return "redirect:/search?msg=Checked+in+" + attendee.getFirstName() + "&orderId=" + attendee.getOrderId();
        } else if (action != null && action.equals("reprintDuringCheckin")) {
            log.info("reprinting badge during check in for {}", attendee);
            try {
                String result = badgePrintService.printBadgesForAttendees(List.of(attendee), settings);
                return "redirect:/reg/checkin/" + attendee.getId() + "/printbadge?msg=" + result;
            } catch (PrintException ex) {
                log.error("Error printing", ex);
                return "redirect:/reg/checkin/" + attendee.getId() + "/printbadge?err=" + ex.getMessage();
            }
        } else {
            throw new RuntimeException("No action found");
        }
    }
}

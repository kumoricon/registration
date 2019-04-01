package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;


/**
 * This controller handles the pre-reg checkin flow, verifying attendee information and
 * printing a badge
 */
@Controller
public class CheckinController {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final UserRepository userRepository;
    private final AttendeeService attendeeService;

    @Autowired
    public CheckinController(AttendeeRepository attendeeRepository, AttendeeHistoryRepository attendeeHistoryRepository, UserRepository userRepository, AttendeeService attendeeService) {
        this.attendeeRepository = attendeeRepository;
        this.userRepository = userRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.attendeeService = attendeeService;
    }

    @RequestMapping(value = "/reg/checkin/{id}")
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String verifyData(Model model,
                         @PathVariable Integer id) {
        try {
            Attendee attendee = attendeeRepository.findById(id);
            model.addAttribute("attendee", attendee);
            model.addAttribute("history", attendeeHistoryRepository.findAllByAttendeeId(id));
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        return "reg/checkin-id";
    }

    @RequestMapping(value = "/reg/checkin/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    @Transactional
    public String checkIn(Model model,
                             @PathVariable Integer id,
                             Principal principal) {
        User currentUser = userRepository.findOneByUsernameIgnoreCase(principal.getName());
        Attendee attendee = attendeeService.checkInAttendee(id, currentUser);
        model.addAttribute("attendee", attendee);

        // TODO: Print badge here

        return "reg/checkin-id-printbadge";
    }

    @RequestMapping(value = "/reg/checkin/{id}/printbadge")
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String printBadge(Model model,
                             @PathVariable Integer id,
                             @RequestParam(required=false , value = "action") String action) {
        Attendee attendee = attendeeRepository.findById(id);
        model.addAttribute("attendee", attendee);
        return "reg/checkin-id-printbadge";
    }

    @RequestMapping(value = "/reg/checkin/{id}/printbadge", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String printBadgeAction(Model model,
                                   @PathVariable Integer id,
                                   @RequestParam(required=false , value = "action") String action) {
        Attendee attendee = attendeeRepository.findById(id);

        if (action != null && action.equals("badgePrintedSuccessfully") && attendee != null) {
            attendee.setBadgePrinted(true);
            attendeeRepository.save(attendee);
            return "redirect:/search?msg=Checked+in+" + attendee.getFirstName() + "&orderId=" + attendee.getOrderId();
        } else if (action != null && action.equals("reprintDuringCheckin")) {
            return "redirect:/reg/checkin/" + attendee.getId() + "/printbadge?msg=Reprinting+Badge";
        } else {
            throw new RuntimeException("No action found");
        }
    }

    private Attendee findAttendee(String id) {
        Attendee attendee;
        try {
            attendee = attendeeRepository.findById(Integer.parseInt(id));
            if (attendee == null) {
                throw new RuntimeException("Attendee " + id + " not found");
            }
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Couldn't convert " + id + " to a number");
        }
        return attendee;
    }

}

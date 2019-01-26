package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
    private final UserRepository userRepository;

    @Autowired
    public CheckinController(AttendeeRepository attendeeRepository, UserRepository userRepository) {
        this.attendeeRepository = attendeeRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/reg/checkin/{id}")
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String verifyData(Model model,
                         @PathVariable String id,
                         @RequestParam(required = false) String err,
                         @RequestParam(required=false) String msg) {
        try {
            Attendee attendee = attendeeRepository.findOneById(Integer.parseInt(id));
            model.addAttribute("attendee", attendee);
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/checkin-id";
    }

    @RequestMapping(value = "/reg/checkin/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String checkIn(Model model,
                             @PathVariable String id,
                             Principal principal,
                             @RequestParam(required = false) String err,
                             @RequestParam(required=false) String msg) {
        Attendee attendee = findAttendee(id);
        attendee.setCheckedIn(true);
        User currentUser = userRepository.findOneByUsernameIgnoreCase(principal.getName());
        attendee.addHistoryEntry(currentUser, "Attendee Checked In");
        attendee.setBadgePrinted(true);
        attendee = attendeeRepository.save(attendee);
        model.addAttribute("attendee", attendee);

        // TODO: Print badge here

        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/checkin-id-printbadge";
    }

    @RequestMapping(value = "/reg/checkin/{id}/printbadge")
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String printBadge(Model model,
                             @PathVariable String id,
                             @RequestParam(required=false , value = "action") String action,
                             @RequestParam(required = false) String err,
                             @RequestParam(required=false) String msg) {
        Attendee attendee = findAttendee(id);
        model.addAttribute("attendee", attendee);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/checkin-id-printbadge";
    }

    @RequestMapping(value = "/reg/checkin/{id}/printbadge", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String printBadgeAction(Model model,
                                   @PathVariable String id,
                                   @RequestParam(required=false , value = "action") String action,
                                   @RequestParam(required = false) String err,
                                   @RequestParam(required=false) String msg) {
        Attendee attendee = findAttendee(id);

        if (action != null && action.equals("badgePrintedSuccessfully") && attendee != null) {
            attendee.setBadgePrinted(true);
            attendeeRepository.save(attendee);
            return "redirect:/search?msg=Checked+in+" + attendee.getFirstName() + "&q=" + attendee.getOrder().getOrderId();
        } else if (action != null && action.equals("reprintDuringCheckin")) {
            return "redirect:/reg/checkin/" + attendee.getId() + "/printbadge?msg=Reprinting+Badge";
        } else {
            throw new RuntimeException("No action found");
        }
    }

    private Attendee findAttendee(String id) {
        Attendee attendee;
        try {
            attendee = attendeeRepository.findOneById(Integer.parseInt(id));
            if (attendee == null) {
                throw new RuntimeException("Attendee " + id + " not found");
            }
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Couldn't convert " + id + " to a number");
        }
        return attendee;
    }

}

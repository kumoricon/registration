package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeHistory;
import org.kumoricon.registration.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.extras.springsecurity5.auth.Authorization;

import java.util.List;


@Controller
public class PrintBadgeController {
    private final AttendeeRepository attendeeRepository;
    private final UserRepository userRepository;

    @Autowired
    public PrintBadgeController(AttendeeRepository attendeeRepository, UserRepository userRepository) {
        this.attendeeRepository = attendeeRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/reg/printbadge/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String printBadge(Model model,
                             @PathVariable String id,
                             @RequestParam(required=false , value = "action") String action,
                             @RequestParam(required = false) String err,
                             @RequestParam(required=false) String msg,
                             Authentication auth) {
        Attendee attendee = null;
        try {
            attendee = attendeeRepository.findOneById(Integer.parseInt(id));
            if (attendee == null) {
                throw new RuntimeException("Attendee " + id + " not found");
            }

            attendee.setCheckedIn(true);
            attendee.addHistoryEntry(userRepository.findOneByUsernameIgnoreCase(auth.getName()), "Checked In");
            attendee = attendeeRepository.save(attendee);
            model.addAttribute("attendee", attendee);
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        if (action != null && action.equals("badgePrintedSuccessfully") && attendee != null) {
            return "redirect:/search?msg=Checked%20in%20" + attendee.getFirstName() + "&q=" + attendee.getOrder().getOrderId();
        } else if (action != null && action.equals("reprintDuringCheckin")) {
            model.addAttribute("msg", "Reprinting Badge");
            return "reg/printbadge-id";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/printbadge-id";
    }
}

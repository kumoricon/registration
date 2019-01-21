package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeHistory;
import org.kumoricon.registration.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class CheckinController {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;

    @Autowired
    public CheckinController(AttendeeRepository attendeeRepository, AttendeeHistoryRepository attendeeHistoryRepository) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }

    @RequestMapping(value = "/reg/checkin/{id}")
    @PreAuthorize("hasAuthority('pre_reg_check_in')")
    public String verifyData(Model model,
                         @PathVariable String id,
                         @RequestParam(required = false) String err,
                         @RequestParam(required=false) String msg) {
        try {
            Attendee attendee = attendeeRepository.findOneById(Integer.parseInt(id));
            List<AttendeeHistory> history = attendeeHistoryRepository.findByAttendee(attendee);
            model.addAttribute("attendee", attendee);
            model.addAttribute("history", history);
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/checkin-id";
    }
}

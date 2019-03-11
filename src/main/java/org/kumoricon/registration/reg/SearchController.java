package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    private final AttendeeSearchRepository attendeeSearchRepository;
    private final AttendeeRepository attendeeRepository;

    @Autowired
    public SearchController(AttendeeSearchRepository attendeeSearchRepository, AttendeeRepository attendeeRepository) {
        this.attendeeSearchRepository = attendeeSearchRepository;
        this.attendeeRepository = attendeeRepository;
    }

    @RequestMapping(value = "/search")
    @PreAuthorize("hasAuthority('attendee_search')")
    public String search(Model model,
                                     @RequestParam(required = false) String q,
                                     @RequestParam(required = false) String err,
                                     @RequestParam(required=false) String msg) {
        List<Attendee> attendees;
        if (q == null || q.trim().isEmpty()) {
            attendees = new ArrayList<>();
        } else {
            model.addAttribute("query", q.trim());
//            attendees = attendeeSearchRepository.searchFor(q.trim().split(" "));
            attendees = new ArrayList<>();
            if (attendees.size() ==0) {
                attendees = attendeeRepository.findByOrderNumber(q);
            }
        }

        model.addAttribute("attendees", attendees);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/search";
    }
}

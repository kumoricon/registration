package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.AttendeeListDTO;
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

    @Autowired
    public SearchController(AttendeeSearchRepository attendeeSearchRepository) {
        this.attendeeSearchRepository = attendeeSearchRepository;
    }

    @RequestMapping(value = "/search")
    @PreAuthorize("hasAuthority('attendee_search')")
    public String search(Model model,
                         @RequestParam(required = false) String q,
                         @RequestParam(required = false) Integer orderId) {
        List<AttendeeListDTO> attendees = new ArrayList<>();

        if (orderId != null) {
            attendees = attendeeSearchRepository.findAllByOrderId(orderId);
        } else {
            if (q != null) {
                attendees = attendeeSearchRepository.searchFor(q.trim().split(" "));
                if (attendees.size() ==0) {
                    attendees = attendeeSearchRepository.searchByOrderNumber(q);
                }
            }
        }

        if (q != null) model.addAttribute("query", q.trim());
        if (orderId != null) model.addAttribute("orderId", orderId);
        model.addAttribute("attendees", attendees);
        return "reg/search";
    }
}

package org.kumoricon.registration.search;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.kumoricon.registration.model.blacklist.BlacklistName;
import org.kumoricon.registration.model.blacklist.BlacklistRepository;
import org.kumoricon.registration.model.blacklist.BlacklistValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class SearchController {
    private final AttendeeSearchRepository attendeeSearchRepository;

    @Autowired
    public SearchController(AttendeeSearchRepository attendeeSearchRepository) {
        this.attendeeSearchRepository = attendeeSearchRepository;
    }

    @RequestMapping(value = "/search")
    @PreAuthorize("hasAuthority('attendee_search')")
    public String listBlacklistNames(Model model,
                                     @RequestParam(required = false) String q,
                                     @RequestParam(required = false) String err,
                                     @RequestParam(required=false) String msg) {
        List<Attendee> attendees;
        if (q == null || q.trim().isEmpty()) {
            attendees = new ArrayList<>();
        } else {
            model.addAttribute("query", q.trim());
            attendees = attendeeSearchRepository.searchFor(q.trim().split(" "));
        }

        model.addAttribute("attendees", attendees);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "search/search";
    }
}

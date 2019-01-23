package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchByBadgeController {
    private final BadgeRepository badgeRepository;
    private final AttendeeRepository attendeeRepository;

    @Autowired
    public SearchByBadgeController(BadgeRepository badgeRepository, AttendeeRepository attendeeRepository) {
        this.badgeRepository = badgeRepository;
        this.attendeeRepository = attendeeRepository;
    }

    @RequestMapping(value = "/searchbybadge")
    @PreAuthorize("hasAuthority('attendee_search')")
    public String search(Model model,
                         @RequestParam(required = false) String badgeName,
                         @RequestParam(required = false) Integer page,
                         @RequestParam(required = false) String err,
                         @RequestParam(required=false) String msg) {

        Integer nextPage, prevPage;
        if (page == null) { page = 0; }
        if (page > 0) {
            prevPage = page-1;
        } else {
            prevPage = null;
        }
        Pageable pageable = PageRequest.of(page, 20);

        List<Badge> badgeTypes = badgeRepository.findAll();

        // There should be a small number of badge types -- faster to search through the
        // whole list than make another database query

        Badge selected = null;
        if (badgeName != null) {
            for (Badge b : badgeTypes) {
                if (badgeName.trim().toLowerCase().equals(b.getName().toLowerCase())) {
                    selected = b;
                }
            }
        }

        List<Attendee> attendees;
        if (selected == null) {
            attendees = new ArrayList<>();
        } else {
            model.addAttribute("badgeName", badgeName.trim().toLowerCase());
            attendees = attendeeRepository.findByBadgeType(selected, pageable);
        }
        if (attendees.size() == 20) {
            nextPage = page + 1;
        } else {
            nextPage = null;
        }
        model.addAttribute("badgeTypes", badgeTypes);
        model.addAttribute("selected", selected);
        model.addAttribute("attendees", attendees);
        model.addAttribute("page", page);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/searchbybadge";
    }
}

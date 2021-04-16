package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.AttendeeListDTO;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchByBadgeController {
    private final BadgeService badgeService;
    private final AttendeeSearchRepository attendeeSearchRepository;

    public SearchByBadgeController(BadgeService badgeService, AttendeeSearchRepository attendeeSearchRepository) {
        this.badgeService = badgeService;
        this.attendeeSearchRepository = attendeeSearchRepository;
    }

    @RequestMapping(value = "/searchbybadge")
    @PreAuthorize("hasAuthority('attendee_search')")
    public String search(Model model,
                         @RequestParam(required = false) String badgeName,
                         @RequestParam(required = false) Integer page) {

        Integer nextPage, prevPage;
        if (page == null) { page = 0; }
        if (page > 0) {
            prevPage = page-1;
        } else {
            prevPage = null;
        }

        List<Badge> badgeTypes = badgeService.findAll();

        // There should be a small number of badge types -- faster to search through the
        // whole list than make another database query

        Badge selected = null;
        if (badgeName != null) {
            for (Badge b : badgeTypes) {
                if (badgeName.trim().equalsIgnoreCase(b.getName())) {
                    selected = b;
                }
            }
        }

        List<AttendeeListDTO> attendees;
        if (selected == null) {
            attendees = new ArrayList<>();
        } else {
            model.addAttribute("badgeName", badgeName.trim().toLowerCase());
            attendees = attendeeSearchRepository.searchByBadgeType(selected.getId(), page);
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
        return "reg/searchbybadge";
    }
}

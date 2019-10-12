package org.kumoricon.registration.reports;

import org.kumoricon.registration.helpers.DateTimeService;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.CheckInByBadgeTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class CheckInReportController {
    private final AttendeeRepository attendeeRepository;
    private final DateTimeService dateTimeService;

    @Autowired
    public CheckInReportController(AttendeeRepository attendeeRepository, DateTimeService dateTimeService) {
        this.attendeeRepository = attendeeRepository;
        this.dateTimeService = dateTimeService;
    }

    @RequestMapping(value = "/reports/checkinbybadgetype")
    @PreAuthorize("hasAuthority('view_check_in_by_badge_type_report')")
    public String admin(Model model) {

        List<CheckInByBadgeTypeDTO> checkInByBadge = attendeeRepository.getCheckInCountsByBadgeType();

        model.addAttribute("byBadgeType", checkInByBadge);
        model.addAttribute("totalCount", attendeeRepository.findTotalAttendeeCount());
        model.addAttribute("warmBodyCount", attendeeRepository.findWarmBodyCount());
        model.addAttribute("fmt", dateTimeService);
        return "reports/checkinbybadgetype";
    }
}

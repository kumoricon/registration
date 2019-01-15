package org.kumoricon.registration.reports;

import org.kumoricon.registration.helpers.DateTimeService;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AttendanceReportController {
    private final AttendeeRepository attendeeRepository;
    private final DateTimeService dateTimeService;

    @Autowired
    public AttendanceReportController(AttendeeRepository attendeeRepository, DateTimeService dateTimeService) {
        this.attendeeRepository = attendeeRepository;
        this.dateTimeService = dateTimeService;
    }

    @RequestMapping(value = "/reports/attendance")
    @PreAuthorize("hasAuthority('view_attendance_report')")
    public String admin(Model model) {
        model.addAttribute("byDate", attendeeRepository.findAtConCheckInCountsByDate());
        model.addAttribute("byBadgeType", attendeeRepository.findBadgeCounts());
        model.addAttribute("totalCount", attendeeRepository.findTotalAttendeeCount());
        model.addAttribute("warmBodyCount", attendeeRepository.findWarmBodyCount());
        model.addAttribute("fmt", dateTimeService);
        return "reports/attendance";
    }
}

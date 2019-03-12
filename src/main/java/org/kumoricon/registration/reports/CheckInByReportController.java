package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class CheckInByReportController {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;


    @Autowired
    public CheckInByReportController(AttendeeRepository attendeeRepository, AttendeeHistoryRepository attendeeHistoryRepository) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }

    @RequestMapping(value = "/reports/checkinbyuser")
    @PreAuthorize("hasAuthority('view_check_in_by_user_report')")
    public String checkInByUser(Model model) {
        model.addAttribute("data", attendeeHistoryRepository.checkInCountByUsers());
        return "reports/checkinbyuser";
    }

    @RequestMapping(value = "/reports/checkinbyhour")
    @PreAuthorize("hasAuthority('view_check_in_by_hour_report')")
    public String checkInByHour(Model model) {
        model.addAttribute("data", attendeeRepository.findCheckInCountsByHour());
        return "reports/checkinbyhour";
    }

}

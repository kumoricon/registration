package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.CheckInByBadgeTypeDTO;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class CheckInReportController {
    private final AttendeeRepository attendeeRepository;
    private final StaffRepository staffRepository;

    public CheckInReportController(AttendeeRepository attendeeRepository,
                                   StaffRepository staffRepository) {
        this.attendeeRepository = attendeeRepository;
        this.staffRepository = staffRepository;
    }

    @RequestMapping(value = "/reports/checkinbybadgetype")
    @PreAuthorize("hasAuthority('view_check_in_by_badge_type_report')")
    public String report(Model model) {

        List<CheckInByBadgeTypeDTO> checkInByBadge = attendeeRepository.getCheckInCountsByBadgeType();

        model.addAttribute("byBadgeType", checkInByBadge);
        model.addAttribute("totalCount", attendeeRepository.findTotalAttendeeCount());
        model.addAttribute("warmBodyCount", attendeeRepository.findWarmBodyCount());
        model.addAttribute("staffNotCheckedIn", staffRepository.countByCheckedIn(false));
        model.addAttribute("staffCheckedIn", staffRepository.countByCheckedIn(true));
        return "reports/checkinbybadgetype";
    }
}

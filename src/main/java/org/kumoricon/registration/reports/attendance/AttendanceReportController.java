package org.kumoricon.registration.reports.attendance;

import org.kumoricon.registration.model.attendee.AttendeeListDTO;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.model.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class AttendanceReportController {
    private final AttendeeSearchRepository attendeeSearchRepository;
    private final StaffRepository staffRepository;
    private final String[] ATTENDEE_BADGE_TYPES = {"wednesday", "thursday", "friday", "saturday", "sunday", "weekend", "vip", "vip-cumulus", "vip-altocumulus", "vip-cumulonimbus"};
    private final String[] VENDOR_BADGE_TYPES = {"artist", "exhibitor"};

    public AttendanceReportController(AttendeeSearchRepository attendeeSearchRepository,
                                      StaffRepository staffRepository) {
        this.attendeeSearchRepository = attendeeSearchRepository;
        this.staffRepository = staffRepository;
    }

    @RequestMapping(value = "/reports/attendance")
    @PreAuthorize("hasAuthority('view_attendance_report')")
    public String attendanceReport(@RequestParam(required = false, defaultValue = "false") Boolean deduplicate,
                                   @AuthenticationPrincipal User principal,
                                   Model model) {

        AttendanceCounts attendanceCounts = new AttendanceCounts();
        List<AttendeeListDTO> attendees = attendeeSearchRepository.findAll();
        List<AttendeeListDTO> duplicates = new ArrayList<>();
        // Limit the deduplicated report to only certain users - it should not be viewed as
        // the "final truth", just a tool. Make sure to inspect the records being excluded!
        if (deduplicate && principal.hasRight("view_attendance_report_deduplicated")) {
            attendees = deduplicateAttendees(attendees, duplicates);
        }
        countList(attendees, attendanceCounts);
        countStaff(attendanceCounts);

        model.addAttribute("counts", attendanceCounts);
        model.addAttribute("deduplicate", deduplicate);
        model.addAttribute("duplicates", duplicates);
        return "reports/attendance";
    }

    private List<AttendeeListDTO> deduplicateAttendees(List<AttendeeListDTO> attendees, List<AttendeeListDTO> multiples) {
        List<AttendeeListDTO> deduplicated = new ArrayList<>();
        HashMap<String, List<AttendeeListDTO>> found = new HashMap<>();

        for (AttendeeListDTO a : attendees) {
            if (a.getBadgeType().contains("Weekend") ||
                a.getBadgeType().contains("VIP") ||
                a.getBadgeType().equalsIgnoreCase("thursday") ||
                a.getBadgeType().equalsIgnoreCase("friday") ||
                a.getBadgeType().equalsIgnoreCase("saturday") ||
                a.getBadgeType().equalsIgnoreCase("sunday")) {
                String matchString = matchString(a);

                if (!found.containsKey(matchString)) {
                    found.put(matchString, new ArrayList<>());
                }
                found.get(matchString).add(a);

            } else {
                deduplicated.add(a);
            }
        }

        for (String key : found.keySet()) {
            deduplicated.add(found.get(key).get(0));
            if (found.get(key).size() > 1) {
                multiples.addAll(found.get(key));
            }
        }

        return deduplicated;
    }

    private String matchString(AttendeeListDTO attendee) {
        StringBuilder sb = new StringBuilder();

        if (attendee.getFirstName() != null) {
            sb.append(attendee.getFirstName().toLowerCase());
        }
        if (attendee.getLastName() != null) {
            sb.append(attendee.getLastName().toLowerCase());
        }
        sb.append(attendee.getBirthDate().toString());
        return sb.toString();
    }

    private void countList(List<AttendeeListDTO> attendeeList, AttendanceCounts attendanceCounts) {
        for (AttendeeListDTO a : attendeeList) {
            attendanceCounts.incrmentTotalConventionMembers();

            if (a.getCheckedIn()) {
                attendanceCounts.incrementWarmBodyCount();
            }

            if (isRegularAttendee(a.getBadgeType()) &&
                    BigDecimal.ZERO.compareTo(a.getPaidAmount()) < 0) {
                attendanceCounts.incrementPaidMembers();
            } else if (isArtistExhibitor(a.getBadgeType())) {
                attendanceCounts.incrementPaidMembers();
            }

            if (isRegularAttendee(a.getBadgeType()) &&
                    BigDecimal.ZERO.compareTo(a.getPaidAmount()) < 0 &&
                    a.getCheckedIn()) {
                attendanceCounts.incrementPaidAttendees();
            }
        }
    }

    private void countAttendees(AttendanceCounts attendanceCounts) {
        List<AttendeeListDTO> members = attendeeSearchRepository.findAll();
        countList(members, attendanceCounts);
    }

    /**
     * Staff count as warm bodies if they've checked in
     * @param attendanceCounts Running count totals
     */
    private void countStaff(AttendanceCounts attendanceCounts) {
        for (Staff s : staffRepository.findAll()) {
            attendanceCounts.incrmentTotalConventionMembers();
            if (s.getCheckedIn()) {
                attendanceCounts.incrementWarmBodyCount();
            }
        }
    }

    private Boolean isRegularAttendee(String badgeType) {
        for (String badge : ATTENDEE_BADGE_TYPES) {
            if (badge.equalsIgnoreCase(badgeType)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isArtistExhibitor(String badgeType) {
        for (String badge : VENDOR_BADGE_TYPES) {
            if (badge.equalsIgnoreCase(badgeType)) {
                return true;
            }
        }
        return false;
    }


}
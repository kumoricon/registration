package org.kumoricon.registration.reports.attendance;

import org.kumoricon.registration.guest.Guest;
import org.kumoricon.registration.guest.GuestRepository;
import org.kumoricon.registration.model.attendee.AttendeeListDTO;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
public class AttendanceReportController {
    private final AttendeeSearchRepository attendeeSearchRepository;
    private final GuestRepository guestRepository;
    private final StaffRepository staffRepository;
    private final String[] ATTENDEE_BADGE_TYPES = {"wednesday", "thursday", "friday", "saturday", "sunday", "weekend", "vip"};
    private final String[] VENDOR_BADGE_TYPES = {"artist", "exhibitor"};

    @Autowired
    public AttendanceReportController(AttendeeSearchRepository attendeeSearchRepository,
                                      GuestRepository guestRepository,
                                      StaffRepository staffRepository) {
        this.attendeeSearchRepository = attendeeSearchRepository;
        this.guestRepository = guestRepository;
        this.staffRepository = staffRepository;
    }

    @RequestMapping(value = "/reports/attendance")
    @PreAuthorize("hasAuthority('view_attendance_report')")
    public String till(Model model) {

        AttendanceCounts attendanceCounts = new AttendanceCounts();
        countAttendees(attendanceCounts);
        countGuests(attendanceCounts);
        countStaff(attendanceCounts);

        model.addAttribute("counts", attendanceCounts);
        return "reports/attendance";
    }

    private void countAttendees(AttendanceCounts attendanceCounts) {
        for (AttendeeListDTO a : attendeeSearchRepository.findAll()) {
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

    /**
     * Assume all guests are checked in, since we don't manage guest checkins through this
     * software. (Only badge reprints)
     * @param attendanceCounts
     */
    private void countGuests(AttendanceCounts attendanceCounts) {
        for (Guest g: guestRepository.findAll()) {
            attendanceCounts.incrmentTotalConventionMembers();
            attendanceCounts.incrementWarmBodyCount();
        }
    }

    /**
     * Staff count as warm bodies if they've checked in
     * @param attendanceCounts
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
        String toCompare = badgeType.toLowerCase();

        for (String badge : ATTENDEE_BADGE_TYPES) {
            if (badge.equals(toCompare)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isArtistExhibitor(String badgeType) {
        String toCompare = badgeType.toLowerCase();

        for (String badge : VENDOR_BADGE_TYPES) {
            if (badge.equals(toCompare)) {
                return true;
            }
        }
        return false;
    }


}
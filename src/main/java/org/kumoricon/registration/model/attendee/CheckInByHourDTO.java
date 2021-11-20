package org.kumoricon.registration.model.attendee;

import java.time.OffsetDateTime;

public class CheckInByHourDTO {
    private final OffsetDateTime start;
    private final Integer attendeePreRegCheckedIn;
    private final Integer attendeeAtConCheckedIn;
    private final Integer vipPreRegCheckedIn;
    private final Integer vipAtConCheckedIn;
    private final Integer specialityPreRegCheckedIn;
    private final Integer specialityAtConCheckedIn;
    private final Integer staffCheckedIn;

    public CheckInByHourDTO(OffsetDateTime start,
                            Integer attendeePreRegCheckedIn, Integer attendeeAtConCheckedIn,
                            Integer vipPreRegCheckedIn, Integer vipAtConCheckedIn,
                            Integer specialtyPreRegCheckedIn, Integer specialityAtConCheckedIn,
                            Integer staffCheckedIn) {
        this.start = start;
        this.attendeePreRegCheckedIn = attendeePreRegCheckedIn;
        this.attendeeAtConCheckedIn = attendeeAtConCheckedIn;
        this.vipPreRegCheckedIn = vipPreRegCheckedIn;
        this.vipAtConCheckedIn = vipAtConCheckedIn;
        this.specialityPreRegCheckedIn = specialtyPreRegCheckedIn;
        this.specialityAtConCheckedIn = specialityAtConCheckedIn;
        this.staffCheckedIn = staffCheckedIn;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public Integer getAttendeePreRegCheckedIn() {
        return attendeePreRegCheckedIn;
    }

    public Integer getAttendeeAtConCheckedIn() {
        return attendeeAtConCheckedIn;
    }

    public Integer getVipPreRegCheckedIn() { return vipPreRegCheckedIn; }

    public Integer getVipAtConCheckedIn() { return vipAtConCheckedIn; }

    public Integer getSpecialityPreRegCheckedIn() { return specialityPreRegCheckedIn; }

    public Integer getSpecialityAtConCheckedIn() { return specialityAtConCheckedIn; }

    public Integer getStaffCheckedIn() { return staffCheckedIn;}

    public Integer getTotal() {
        return attendeePreRegCheckedIn + attendeeAtConCheckedIn + vipPreRegCheckedIn + vipAtConCheckedIn +
                specialityPreRegCheckedIn + specialityAtConCheckedIn + staffCheckedIn;
    }
}

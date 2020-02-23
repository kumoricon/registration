package org.kumoricon.registration.model.attendee;

import java.time.ZonedDateTime;

public class CheckInByHourDTO {
    private final ZonedDateTime start;
    private final Integer preregCheckIn;
    private final Integer atConCheckIn;

    public CheckInByHourDTO(ZonedDateTime start, Integer preregCheckIn, Integer atConCheckIn) {
        this.start = start;
        this.preregCheckIn = preregCheckIn;
        this.atConCheckIn = atConCheckIn;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public Integer getPreregCheckIn() {
        return preregCheckIn;
    }

    public Integer getAtConCheckIn() {
        return atConCheckIn;
    }

    public Integer getTotal() {
        return preregCheckIn + atConCheckIn;
    }
}

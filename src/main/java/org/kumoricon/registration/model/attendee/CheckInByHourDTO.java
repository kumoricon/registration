package org.kumoricon.registration.model.attendee;

import java.time.OffsetDateTime;

public class CheckInByHourDTO {
    private final OffsetDateTime start;
    private final Integer preregCheckIn;
    private final Integer atConCheckIn;

    public CheckInByHourDTO(OffsetDateTime start, Integer preregCheckIn, Integer atConCheckIn) {
        this.start = start;
        this.preregCheckIn = preregCheckIn;
        this.atConCheckIn = atConCheckIn;
    }

    public OffsetDateTime getStart() {
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

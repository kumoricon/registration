package org.kumoricon.registration.model.attendee;

import java.time.Instant;

public class CheckInByHourDTO {
    private Instant start;
    private Integer preregCheckIn;
    private Integer atConCheckIn;

    public CheckInByHourDTO(Instant start, Integer preregCheckIn, Integer atConCheckIn) {
        this.start = start;
        this.preregCheckIn = preregCheckIn;
        this.atConCheckIn = atConCheckIn;
    }

    public Instant getStart() {
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

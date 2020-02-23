package org.kumoricon.registration.model.attendee;

import java.time.ZonedDateTime;

public class AttendeeHistoryDTO {
    private final ZonedDateTime timestamp;
    private final String username;
    private final String message;
    private final Integer attendeeId;
    private final String attendeeName;

    public AttendeeHistoryDTO(ZonedDateTime timestamp, String username, String message, Integer attendeeId, String attendeeName) {
        this.timestamp = timestamp;
        this.username = username;
        this.message = message;
        this.attendeeId = attendeeId;
        this.attendeeName = attendeeName;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Integer getAttendeeId() { return attendeeId; }

    public String getAttendeeName() { return attendeeName; }
}

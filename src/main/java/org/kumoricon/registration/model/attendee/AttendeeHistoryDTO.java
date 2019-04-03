package org.kumoricon.registration.model.attendee;

import java.time.ZonedDateTime;

public class AttendeeHistoryDTO {
    private ZonedDateTime timestamp;
    private String username;
    private String message;

    public AttendeeHistoryDTO(ZonedDateTime timestamp, String username, String message) {
        this.timestamp = timestamp;
        this.username = username;
        this.message = message;
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
}

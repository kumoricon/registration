package org.kumoricon.registration.model.attendee;

import org.kumoricon.registration.model.user.User;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Represents a timestamped message or event that is associated with an attendee. May be
 * generated (for example, when they check in) or entered manually by a user.
 */
public class AttendeeHistory {
    private Integer id;
    private Instant timestamp;
    private Integer userId;
    private Integer attendeeId;
    private String message;

    public AttendeeHistory() {}

    public AttendeeHistory(User user, Attendee attendee, String message) {
        this.userId = user.getId();
        this.message = message;
        this.attendeeId = attendee.getId();
        this.timestamp = Instant.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUserId() { return userId; }
    public void setUser(User user) { this.userId = user.getId(); }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getAttendeeId() { return attendeeId; }
    public void setAttendee(Attendee attendee) { this.attendeeId = attendee.getId(); }
    public void setAttendeeId(Integer attendeeId) { this.attendeeId = attendeeId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String toString() {
        String truncatedMessage = "";
        if (message != null) {
            truncatedMessage = message.length() > 100 ? message.substring(0, 100) + "..." : message;
        }
        if (id != null) {
            return String.format("[History %s: %s %s]", id, timestamp, truncatedMessage);
        } else {
            return String.format("[History: %s %s]", timestamp, truncatedMessage);
        }
    }

    public void setTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            this.timestamp = null;
        } else {
            this.timestamp = timestamp.toInstant();
        }
    }
}

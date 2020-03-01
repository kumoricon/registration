package org.kumoricon.registration.model.attendee;

import org.kumoricon.registration.model.user.User;

import java.time.OffsetDateTime;

/**
 * Represents a timestamped message or event that is associated with an attendee. May be
 * generated (for example, when they check in) or entered manually by a user.
 */
public class AttendeeHistory {
    private Integer id;
    private OffsetDateTime timestamp;
    private Integer userId;
    private Integer attendeeId;
    private String message;

    public AttendeeHistory() {}

    public AttendeeHistory(User user, Integer attendeeId, String message) {
        this.userId = user.getId();
        this.attendeeId = attendeeId;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
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
}

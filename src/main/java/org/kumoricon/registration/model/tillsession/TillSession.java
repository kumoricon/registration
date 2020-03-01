package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.user.User;

import java.time.OffsetDateTime;

public class TillSession {
    private Integer id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private Integer userId;
    private String tillName;        // The name of the till or computer the session was at. Usually "1", "2", etc
    private boolean open;

    public TillSession() {}

    /**
     * Generate an open session for the given user starting now
     * @param user User
     */
    public TillSession(User user, String tillName) {
        setUserId(user.getId());
        setStartTime(OffsetDateTime.now());
        setTillName(tillName);
        setOpen(true);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) {
        if (this.startTime == null) {
            this.startTime = startTime;
        }
    }

    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) {
        if (this.endTime == null) {
            this.endTime = endTime;
        }
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    public String getTillName() {
        return tillName;
    }

    public void setTillName(String tillName) {
        this.tillName = tillName;
    }

    @Override
    public String toString() {
        return String.format("[Session: %s]", id);
    }
}

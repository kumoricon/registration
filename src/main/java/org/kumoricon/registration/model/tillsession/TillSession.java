package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.user.User;

import java.time.Instant;

public class TillSession {
    private Integer id;
    private Instant startTime;
    private Instant endTime;
    private User user;
    private Integer userId;
    private boolean open;

    public TillSession() {}

    /**
     * Generate an open session for the given user starting now
     * @param user User
     */
    public TillSession(User user) {
        setUser(user);
        setUserId(user.getId());
        setStartTime(Instant.now());
        setOpen(true);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) {
        if (this.startTime == null) {
            this.startTime = startTime;
        }
    }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) {
        if (this.endTime == null) {
            this.endTime = endTime;
        }
    }

    public User getUser() { return user; }
    public void setUser(User user) {
        if (this.user == null) {
            this.user = user;
        }
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    @Override
    public String toString() {
        return String.format("[Session: %s]", id);
    }
}

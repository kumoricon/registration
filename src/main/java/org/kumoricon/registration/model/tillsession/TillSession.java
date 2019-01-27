package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.Record;
import org.kumoricon.registration.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "sessions")
public class TillSession extends Record {
    private Instant startTime;
    private Instant endTime;
    @NotNull
    @ManyToOne
    private User user;
    private boolean open;

    public TillSession() {}

    /**
     * Generate an open session for the given user starting now
     * @param user User
     */
    public TillSession(User user) {
        setUser(user);
        setStartTime(Instant.now());
        setOpen(true);
    }

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

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    @Override
    public String toString() {
        return String.format("[Session: %s]", id);
    }
}

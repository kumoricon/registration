package org.kumoricon.registration.model.loginsession;

import java.time.Instant;

public class LoginTimePeriod {
    private Instant startTime;
    private String user;

    public LoginTimePeriod(Instant startTime, String user) {
        this.startTime = startTime;
        this.user = user;
    }

    public Instant getStartTime() { return startTime; }
    public String getUser() { return user; }
}

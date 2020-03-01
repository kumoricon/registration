package org.kumoricon.registration.model.loginsession;

import java.time.OffsetDateTime;

public class LoginTimePeriod {
    private final OffsetDateTime startTime;
    private final String user;

    public LoginTimePeriod(OffsetDateTime startTime, String user) {
        this.startTime = startTime;
        this.user = user;
    }

    public OffsetDateTime getStartTime() { return startTime; }
    public String getUser() { return user; }
}

package org.kumoricon.registration.model.loginsession;

import java.time.Instant;

public class LoginSessionRecord {
    private final Instant start;
    private final Integer userId;

    public LoginSessionRecord(Instant start, Integer userId) {
        this.start = start;
        this.userId = userId;
    }

    public Instant getStart() {
        return start;
    }

    public Integer getUserId() {
        return userId;
    }
}

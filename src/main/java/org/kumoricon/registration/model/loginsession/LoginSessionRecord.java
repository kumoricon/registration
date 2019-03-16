package org.kumoricon.registration.model.loginsession;

import java.time.Instant;

public class LoginSessionRecord {
    private Instant start;
    private Integer userId;

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

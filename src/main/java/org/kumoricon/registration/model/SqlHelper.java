package org.kumoricon.registration.model;

import java.sql.Timestamp;
import java.time.Instant;

public class SqlHelper {
    public static Timestamp translate(Instant instant) {
        if (instant == null) return null;
        return Timestamp.from(instant);
    }

    public static Instant translate(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toInstant();
    }

}

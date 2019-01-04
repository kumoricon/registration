package org.kumoricon.registration.helpers;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Helper to format dates in the correct timezone
 */
@Service
public class DateTimeService {

    private static final ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a").withZone(zoneId);


    public String epochToDateString(Long milliseconds) {
        Instant t = Instant.ofEpochMilli(milliseconds);
        return DATE_TIME_FORMATTER.format(t);
    }

    public String epochToDuration(Long milliseconds) {
        Instant now = Instant.now();

        Duration duration = Duration.between(now, Instant.ofEpochMilli(milliseconds));

        StringBuilder output = new StringBuilder();

        if (duration.toDaysPart() != 0) {
            output.append(Math.abs(duration.toDaysPart())).append(" days ");
        }

        if (duration.toHoursPart() != 0) {
            output.append(String.format("%2d", Math.abs(duration.toHoursPart()))).append(":");
        }

        output.append(String.format("%02d", Math.abs(duration.toMinutesPart()))).append(":");
        output.append(String.format("%02d", Math.abs(duration.toSecondsPart())));

        if (duration.toDaysPart() < 0 || duration.toHoursPart() < 0 || duration.toMinutesPart() < 0 || duration.toSecondsPart() < 0) {
            output.append(" ago");
        }
        return output.toString();
    }
}

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

    public String format(Instant instant) {
        if (instant == null) return "";
        return DATE_TIME_FORMATTER.format(instant.atZone(zoneId));
    }

    public String epochToDateString(Long milliseconds) {
        Instant t = Instant.ofEpochMilli(milliseconds);
        return DATE_TIME_FORMATTER.format(t);
    }

    public String epochToDuration(Long milliseconds) {
        return epochToDuration(milliseconds, Instant.now().toEpochMilli());
    }

    public String epochToDuration(Long start, Long end) {
        assert(start != null && end != null) : "start and end may not be null";
        assert(start <= end) : "start must be <= end";

        if (end-start < 1000) { return "Now"; } // Less than 1 second is pretty much now

        Duration duration = Duration.between(Instant.ofEpochMilli(start), Instant.ofEpochMilli(end));

        StringBuilder output = new StringBuilder();

        if (duration.toDaysPart() > 1) {
            output.append(duration.toDaysPart()).append(" days ");
        } else if (duration.toDaysPart() == 1) {
            output.append("1 day ");
        }

        if (duration.toHoursPart() > 0) {
            output.append(duration.toHoursPart()).append(":");
            output.append(String.format("%02d", duration.toMinutesPart())).append(":");
            output.append(String.format("%02d", duration.toSecondsPart()));
        } else if (duration.toMinutesPart() > 1) {
            output.append(duration.toMinutesPart()).append(" minutes");
        } else if (duration.toMinutesPart() == 1) {
            output.append("1 minute");
        } else if (duration.toSecondsPart() > 1) {
            output.append(duration.toSecondsPart()).append(" seconds");
        } else if (duration.toSecondsPart() == 1) {
            output.append("1 second");
        }

        output.append(" ago");
        return output.toString();
    }
}

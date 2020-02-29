package org.kumoricon.registration.helpers;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class DateTimeServiceTest {
    private final DateTimeService dateTimeService = new DateTimeService();

    @Test
    public void format() {
        assertEquals("12/31/1969 04:00:00 PM", dateTimeService.format(Instant.ofEpochMilli(0L)));
    }

    @Test
    public void epochToDateString() {
        assertEquals("12/31/1969 04:00:00 PM", dateTimeService.epochToDateString(0L));
    }

    @Test
    public void epochToDurationForward() {
        assertEquals("1 day 1:00:00 ago",
                dateTimeService.epochToDuration(0L, 90000000L));
        assertEquals("20 days 20:02:30 ago", dateTimeService.epochToDuration(0L, 1800150500L));
        assertEquals("8 minutes ago", dateTimeService.epochToDuration(0L, 500000L));
        assertEquals("1 minute ago", dateTimeService.epochToDuration(0L, 65000L));
        assertEquals("30 seconds ago", dateTimeService.epochToDuration(0L, 30000L));
        assertEquals("1 second ago", dateTimeService.epochToDuration(0L, 1000L));
    }

    @Test(expected = AssertionError.class)
    public void epochToDurationBackwards() {
        assertEquals("", dateTimeService.epochToDuration(10L, 0L));
    }

    @Test(expected = AssertionError.class)
    public void epochToDurationNull() {
        dateTimeService.epochToDuration(null);
    }

    @Test
    public void epochToDurationSame() {
        assertEquals("Now", dateTimeService.epochToDuration(50L, 50L));
    }
}
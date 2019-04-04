package org.kumoricon.registration.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class LocalDateToStringFormatter implements Formatter<LocalDate> {
    private static final ZoneId TIMEZONE = ZoneId.of("America/Los_Angeles");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DEFAULT = DateTimeFormatter.ofPattern("MMddyyyy");
    private static final DateTimeFormatter DASHES = DateTimeFormatter.ofPattern("M-d-yyyy");
    private static final DateTimeFormatter TWO_DIGIT_YEAR =
            new DateTimeFormatterBuilder()
                    .appendPattern("MMdd")
                    .appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 4, LocalDate.now(TIMEZONE).minusYears(99))
                    .toFormatter();
    private static final DateTimeFormatter TWO_DIGIT_YEAR_SLASHES =
            new DateTimeFormatterBuilder()
                    .appendPattern("M/d/")
                    .appendValueReduced(ChronoField.YEAR_OF_ERA, 2, 4, LocalDate.now(TIMEZONE).minusYears(99))
                    .toFormatter();
    private static final DateTimeFormatter[] FORMATTERS = {ISO, DEFAULT, TWO_DIGIT_YEAR_SLASHES, DASHES, TWO_DIGIT_YEAR};

    private final Logger log = LoggerFactory.getLogger(LocalDateToStringFormatter.class);


    @Override
    public LocalDate parse(String text, Locale locale) {
        if (text == null) return null;
        LocalDate date;

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                date = LocalDate.parse(text, formatter);
                if (date != null) return date;
            } catch (DateTimeParseException ignored) {}
        }
        log.warn("Unable to convert the string {} to a LocalDate. Returning null", text);
        return null;
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return ISO.format(object);
    }
}

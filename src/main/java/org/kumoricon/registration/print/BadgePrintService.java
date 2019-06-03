package org.kumoricon.registration.print;


import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeFactory;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.print.formatter.BadgePrintFormatter;
import org.kumoricon.registration.print.formatter.FullBadgePrintFormatter;
import org.kumoricon.registration.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class BadgePrintService extends PrintService {
    @Value("${kumoreg.printing.enablePrintingFromServer}")
    protected Boolean enablePrintingFromServer;
    private final BadgeService badgeService;

    public BadgePrintService(PrinterInfoService printerInfoService, BadgeService badgeService) {
        super(printerInfoService);
        this.badgeService = badgeService;
    }

    /**
     * Prints badges for a given list of attendees to either the appropriate printer name (from
     * the computers table), or the default printer on the server with the given offset
     * @param attendees List of attendees
     * @return String Result message
     * @throws PrintException Printer error
     */
    public String printBadgesForAttendees(List<Attendee> attendees, PrinterSettings printerSettings) throws PrintException {

        if (enablePrintingFromServer != null && !enablePrintingFromServer) {
            return("Printing from server not enabled");
        }

        if (attendees.size() > 0) {
            List<AttendeeBadgeDTO> attendeeBadgeDTOS = attendeeBadgeDTOsFromAttendees(attendees);
            BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(attendeeBadgeDTOS, printerSettings.getxOffset(), printerSettings.getyOffset());

            boolean setDuplexOn = false;
            printDocument(badgePrintFormatter.getStream(), printerSettings.getPrinterName(), setDuplexOn);
            return String.format("Printed %s badges to %s.",
                    attendees.size(),
                    printerSettings.getPrinterName());
        } else {
            return "No badges to print. Pre-printed badges ready for pickup.";
        }
    }

    public String printTestBadge(PrinterSettings settings) throws PrintException {
        Attendee attendee = new Attendee();
        attendee.setId(100000);
        attendee.setFirstName("Firstname");
        attendee.setLastName("Lastname");
        attendee.setPreferredPronoun("He/Him/His");
        attendee.setFanName("Fan Name - Adult");
        attendee.setBadgeNumber("TST12340");
        attendee.setBadgeId(1);
        attendee.setBirthDate(LocalDate.of(1980, 1, 1));

        return printBadgesForAttendees(List.of(attendee), settings);
    }

    private List<AttendeeBadgeDTO> attendeeBadgeDTOsFromAttendees(@NotNull List<Attendee> attendees) {
        List<AttendeeBadgeDTO> badgeDTOS = new ArrayList<>();

        for (Attendee attendee : attendees) {
            Badge badge = badgeService.findById(attendee.getBadgeId());
            badgeDTOS.add(AttendeeBadgeDTO.fromAttendee(attendee, badge));
        }
        return badgeDTOS;
    }
}

package org.kumoricon.registration.print;


import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.print.formatter.BadgePrintFormatter;
import org.kumoricon.registration.print.formatter.FullBadgePrintFormatter;
import org.kumoricon.registration.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.*;
import java.util.List;


@Service
public class BadgePrintService extends PrintService {
    @Value("${kumoreg.printing.enablePrintingFromServer}")
    protected Boolean enablePrintingFromServer;

    public BadgePrintService(PrinterInfoService printerInfoService) {
        super(printerInfoService);
    }

    /**
     * Prints badges for a given list of attendees to either the appropriate printer name (from
     * the computers table), or the default printer on the server with the given offset
     * @param attendees List of attendees
     * @return String Result message
     * @throws PrintException Printer error
     */
    public String printBadgesForAttendees(List<AttendeeBadgeDTO> attendees, PrinterSettings printerSettings) throws PrintException {

        if (enablePrintingFromServer != null && !enablePrintingFromServer) {
            return("Printing from server not enabled");
        }

        if (attendees.size() > 0) {
            BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(attendees, printerSettings.getxOffset(), printerSettings.getyOffset());

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
        AttendeeBadgeDTO testBadge = new AttendeeBadgeDTO(); // Defaults set in constructor
        testBadge.setId(1);
        testBadge.setFirstName("Firstname");
        testBadge.setLastName("Lastname");
        testBadge.setFanName("Fan Name");
        return printBadgesForAttendees(List.of(testBadge), settings);
    }
}

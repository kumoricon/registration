package org.kumoricon.registration.print;


import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.guest.Guest;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.badge.BadgeType;
import org.kumoricon.registration.model.staff.*;
import org.kumoricon.registration.print.formatter.BadgePrintFormatter;
import org.kumoricon.registration.print.formatter.FullBadgePrintFormatter;
import org.kumoricon.registration.print.formatter.StaffBadgePrintFormatter;
import org.kumoricon.registration.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.kumoricon.registration.print.formatter.badgeimage.BadgeCreatorAttendee;
import org.kumoricon.registration.print.formatter.badgeimage.BadgeCreatorAttendeeFull;
import org.kumoricon.registration.print.formatter.badgeimage.BadgeCreatorStaffFront;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class BadgePrintService extends PrintService {
    @Value("${kumoreg.printing.enablePrintingFromServer}")
    protected Boolean enablePrintingFromServer;
    private final BadgeService badgeService;
    private final BadgeImageService badgeImageService;
    private final BadgeResourceService badgeResourceService;

    public BadgePrintService(PrinterInfoService printerInfoService,
                             BadgeService badgeService,
                             BadgeImageService badgeImageService,
                             BadgeResourceService badgeResourceService) {
        super(printerInfoService);
        this.badgeService = badgeService;
        this.badgeImageService = badgeImageService;
        this.badgeResourceService = badgeResourceService;
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
            try (InputStream pdfStream = generateAttendeePDF(attendees, printerSettings)) {
                printDocument(pdfStream, printerSettings.getPrinterName(), false);
                return String.format("Printed %s badges to %s.",
                        attendees.size(),
                        printerSettings.getPrinterName());
            } catch (IOException e) {
                throw new PrintException(e);
            }
        } else {
            return "No badges to print. Pre-printed badges ready for pickup.";
        }
    }

    public String printBadgesForStaff(List<Staff> staff, PrinterSettings printerSettings) throws PrintException {
        if (enablePrintingFromServer != null && !enablePrintingFromServer) {
            return("Printing from server not enabled");
        }

        if (staff.size() > 0) {
            try (InputStream pdfStream = generateStaffPDF(staff, printerSettings)) {
                printDocument(pdfStream, printerSettings.getPrinterName(), true);
                return String.format("Printed %s badges to %s.",
                        staff.size(),
                        printerSettings.getPrinterName());
            } catch (IOException e) {
                throw new PrintException(e);
            }
        } else {
            return "No badges to print. Pre-printed badges ready for pickup.";
        }
    }

    public String printBadgesForGuest(List<Guest> guests, PrinterSettings printerSettings) throws PrintException {
        if (enablePrintingFromServer != null && !enablePrintingFromServer) {
            return("Printing from server not enabled");
        }

        if (guests.size() > 0) {
            try (InputStream pdfStream = generateGuestPDF(guests, printerSettings)) {
                printDocument(pdfStream, printerSettings.getPrinterName(), true);
                return String.format("Printed %s badges to %s.",
                        guests.size(),
                        printerSettings.getPrinterName());
            } catch (IOException e) {
                throw new PrintException(e);
            }
        } else {
            return "No badges to print. Pre-printed badges ready for pickup.";
        }
    }

    public InputStream generateGuestPDF(List<Guest> guests, PrinterSettings printerSettings) {
        List<StaffBadgeDTO> staffBadgeDTOS = staffBadgeDTOsFromGuest(guests);
        return generateStaffPDF(staffBadgeDTOS, printerSettings, badgeResourceService.getGuestBadgeResource());
    }

    public InputStream generateStaffPDF(List<StaffBadgeDTO> staffBadgeDTOS, PrinterSettings printerSettings, BadgeResource badgeResource) {
        BadgePrintFormatter badgePrintFormatter = new StaffBadgePrintFormatter(staffBadgeDTOS,
                printerSettings.getxOffset(), printerSettings.getyOffset(),
                badgeResource);
        return badgePrintFormatter.getStream();
    }

    public InputStream generateStaffPDF(List<Staff> staff, PrinterSettings printerSettings) {
        List<StaffBadgeDTO> staffBadgeDTOS = staffBadgeDTOsFromStaff(staff);
        return generateStaffPDF(staffBadgeDTOS, printerSettings, badgeResourceService.getStaffBadgeResources());
    }

    public InputStream generateAttendeePDF(List<Attendee> attendees, PrinterSettings printerSettings) {
        List<AttendeeBadgeDTO> attendeeBadgeDTOS = attendeeBadgeDTOsFromAttendees(attendees);
        BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(attendeeBadgeDTOS,
                printerSettings.getxOffset(), printerSettings.getyOffset(),
                badgeResourceService.getAttendeeBadgeResources());
        return badgePrintFormatter.getStream();
    }

    public InputStream generateAttendeePDFfromDTO(List<AttendeeBadgeDTO> attendees, PrinterSettings printerSettings, BadgeType badgeType) {
        BadgeResource badgeResource = badgeResourceService.getBadgeResourceFor(badgeType);

        BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(attendees,
                printerSettings.getxOffset(), printerSettings.getyOffset(),
                badgeResource);
        return badgePrintFormatter.getStream();
    }

    public byte[] generateStaffImage(List<Staff> staff) {
        List<StaffBadgeDTO> staffBadgeDTOS = staffBadgeDTOsFromStaff(staff);
        BadgeCreatorStaffFront badgeCreatorStaffFront = new BadgeCreatorStaffFront(badgeResourceService.getBadgeFont(), badgeResourceService.getNameFont());
        return badgeCreatorStaffFront.createBadge(staffBadgeDTOS.get(0));
    }

    public byte[] generateGuestImage(List<Guest> guests) {
        List<StaffBadgeDTO> staffBadgeDTOS = staffBadgeDTOsFromGuest(guests);
        BadgeCreatorStaffFront badgeCreatorStaffFront = new BadgeCreatorStaffFront(badgeResourceService.getBadgeFont(), badgeResourceService.getNameFont());
        return badgeCreatorStaffFront.createBadge(staffBadgeDTOS.get(0));
    }

    public byte[] generateAttendeeImage(AttendeeBadgeDTO attendeeBadgeDTO) {
        BadgeCreatorAttendee badgeCreatorAttendee = new BadgeCreatorAttendeeFull(badgeResourceService.getBadgeFont(), badgeResourceService.getNameFont());
        return badgeCreatorAttendee.createBadge(attendeeBadgeDTO);
    }

    public String printTestBadge(PrinterSettings settings) throws PrintException {
        Attendee attendee = new Attendee();
        attendee.setId(100000);
        attendee.setFirstName("Firstname");
        attendee.setLastName("Lastname");
        attendee.setPreferredPronoun("They/Them");
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

    private List<StaffBadgeDTO> staffBadgeDTOsFromStaff(@NotNull List<Staff> staffList) {
        List<StaffBadgeDTO> staffBadgeDTOS = new ArrayList<>();
        for (Staff s : staffList) {
            StaffBadgeDTO sb = new StaffBadgeDTO.Builder()
                    .withFirstName(s.getFirstName())
                    .withLastName(s.getLastName())
                    .withAgeRange(s.getAgeCategoryAtCon())
                    .withDepartment(s.getDepartment())
                    .withDepartmentBackgroundColor(s.getDepartmentColorCode())
                    .withPositions(s.getPositions().toArray(new String[0]))
                    .withPreferredPronoun(s.getPreferredPronoun())
                    .withAgeBackgroundColor(getAgeStripeColor(s.getAgeCategoryAtCon()))
                    .withAgeImage(getAgeImage(s.getAgeCategoryAtCon()))
                    .withHideDepartment(s.getSuppressPrintingDepartment())
                    .withBadgeImage(badgeImageService.getBadgeForUuid(s.getUuid(), s.getBadgeImageFileType()))
                    .build();
            staffBadgeDTOS.add(sb);
        }
        return staffBadgeDTOS;
    }

    public List<StaffBadgeDTO> staffBadgeDTOsFromGuest(@NotNull List<Guest> guestList) {
        List<StaffBadgeDTO> staffBadgeDTOS = new ArrayList<>();
        for (Guest g : guestList) {
            StaffBadgeDTO sb = new StaffBadgeDTO.Builder()
                    .withFirstName(g.getFirstName())
                    .withLastName(g.getLastName())
                    .withFanName(g.getFanName())
                    .withAgeRange(g.getAgeCategoryAtCon())
                    .withDepartmentBackgroundColor(null)
                    .withPreferredPronoun(g.getPreferredPronoun())
                    .withAgeBackgroundColor(getAgeStripeColor(g.getAgeCategoryAtCon()))
                    .withAgeImage(getAgeImage(g.getAgeCategoryAtCon()))
                    // Guests don't have badge images, but load the mascot
                    .withBadgeImage(badgeImageService.getMascotImage())
                    .withHideDepartment(true)
                    .build();
            staffBadgeDTOS.add(sb);
        }
        return staffBadgeDTOS;
    }


    private String getAgeStripeColor(String ageRangeAtCon) {
        if (ageRangeAtCon == null) { ageRangeAtCon = "child"; }
        if ("adult".equals(ageRangeAtCon.toLowerCase())) {
            return "#323E99";
        } else if ("youth".equals(ageRangeAtCon.toLowerCase())) {
            return  "#FFFF00";
        } else {
            return  "#CC202A";
        }
    }

    private Image getAgeImage(String ageRangeAtCon) {
        if (ageRangeAtCon == null) { ageRangeAtCon = "child"; }
        if ("adult".equals(ageRangeAtCon.toLowerCase())) {
            return badgeResourceService.getAdultSeal();
        } else if ("youth".equals(ageRangeAtCon.toLowerCase())) {
            return badgeResourceService.getYouthSeal();
        } else {
            return badgeResourceService.getChildSeal();
        }
    }

}

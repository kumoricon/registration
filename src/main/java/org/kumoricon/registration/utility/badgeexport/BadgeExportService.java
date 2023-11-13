package org.kumoricon.registration.utility.badgeexport;

import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.guest.Guest;
import org.kumoricon.registration.guest.GuestRepository;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.kumoricon.registration.print.BadgePrintService;
import org.kumoricon.registration.print.Sides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

@Component
public class BadgeExportService {
    private static final Logger log = LoggerFactory.getLogger(BadgeExportService.class);

    private final AttendeeRepository attendeeRepository;
    private final GuestRepository guestRepository;
    private final StaffRepository staffRepository;
    private final BadgePrintService badgePrintService;

    public BadgeExportService(AttendeeRepository attendeeRepository, GuestRepository guestRepository, StaffRepository staffRepository,
                              BadgePrintService badgePrintService) {
        this.attendeeRepository = attendeeRepository;
        this.guestRepository = guestRepository;
        this.staffRepository = staffRepository;
        this.badgePrintService = badgePrintService;
    }

    public void delegateBadgeExport(BadgeExport badgeExport, PrinterSettings printerSettings) {
        switch (badgeExport.getType()) {
            case "Vip" -> exportAttendeeBadgesByType(badgeExport.getPath(), badgeExport.getType(),
                    badgeExport.isMarkPreprinted(), badgeExport.isWithAttendeeBackground(), printerSettings);
            case "Staff" -> exportStaffBadges(badgeExport.getPath(), printerSettings);
            case "Guest" -> exportGuestBadges(badgeExport.getPath(), printerSettings);
            case "Attendee", "Specialty" -> log.warn("{} badge export not supported", badgeExport.getType());
            default -> log.error("{} badge type not recognized", badgeExport.getType());
        }
    }

    public void exportAttendeeBadgesByType(String dir, String attendeeType, boolean markPreprinted,
                                           boolean withAttendeeBackground, PrinterSettings printerSettings) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        List<Attendee> attendees = getAttendeeListByType(attendeeType);

        // allows us to iterate at least once to run export badge flow if list size < 50
        int iterateAmount = attendees.size() < 50 ? 0 : attendees.size() / 50;

        for (int i = 0; i <= iterateAmount; i++) {
            int start = i*50;
            int end = (i+1)*50;
            if (end > attendees.size()) {
                end = attendees.size();
            }

            File file = Paths.get(dir, attendeeType + "-badges-" + i + ".pdf").toFile();
            log.info("saving badges {}-{} to {}", start+1, end, file.getPath());
            // Sublist is inclusive of the start index, exclusive of the end index
            List<Attendee> sublist = attendees.subList(start, end);

            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return writeAttendeeBadgesToPdf(sublist, file, withAttendeeBackground, printerSettings);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }));
        }

        List<String> results = futures.stream().map(CompletableFuture::join).toList();
        for (final String s : results) {
            log.info(s);
        }

        if (markPreprinted) {
            setAttendeesPreprinted(attendees);
        }
        log.info("All done!");
    }

    public void exportStaffBadges(String dir, PrinterSettings printerSettings) {
        List<CompletableFuture<String>> futures = new ArrayList<>();

        int page = 1;
        boolean keepRunning = true;
        while (keepRunning) {
            File file = Paths.get(dir, "staff-badges-" + page + ".pdf").toFile();
            List<Staff> staffList = staffRepository.findAllWithPositions((page-1)*50);

            futures.add(CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return writeStaffBadgesToPdf(staffList, file, printerSettings);
                        } catch (IOException e) {
                            throw new CompletionException(e);
                        }
                    }));

            if (staffList.size() < 50) {
                keepRunning = false;
            }
            page += 1;
        }

        List<String> results = futures.stream().map(CompletableFuture::join).toList();
        for (String s : results) {
            log.info(s);
        }

        log.info("All done!");
    }

    public void exportGuestBadges(String dir, PrinterSettings printerSettings) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        List<Guest> guests = guestRepository.findAll();

        // allows us to iterate at least once to run export badge flow if list size < 50
        int iterateAmount = guests.size() < 50 ? 0 : guests.size() / 50;

        for (int i = 0; i <= iterateAmount; i++) {
            int start = i*50;
            int end = (i+1)*50;
            if (end > guests.size()) {
                end = guests.size();
            }

            File file = Paths.get(dir, "guest-badges-" + i + ".pdf").toFile();
            log.info("saving badges {}-{} to {}", start+1, end, file.getPath());
            List<Guest> sublist = guests.subList(i*50, end);

            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return writeGuestBadgesToPdf(sublist, file, printerSettings);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }));
        }

        List<String> results = futures.stream().map(CompletableFuture::join).toList();
        for (String s : results) {
            log.info(s);
        }

        log.info("All done!");
    }

    private String writeStaffBadgesToPdf(List<Staff> staff, File file, PrinterSettings printerSettings) throws IOException {
        long startTime = System.currentTimeMillis();
        InputStream pdfStream = badgePrintService.generateStaffPDF(staff, Sides.BOTH, printerSettings);

        writePdf(file, pdfStream);

        long endTime = System.currentTimeMillis();
        return String.format("Wrote %s staff to %s in %s ms", staff.size(), file, endTime - startTime);
    }

    private String writeAttendeeBadgesToPdf(List<Attendee> attendees, File file, boolean withAttendeeBackground,
                                            PrinterSettings printerSettings) throws IOException {
        long startTime = System.currentTimeMillis();
        InputStream pdfStream = badgePrintService.generateAttendeePDF(attendees, withAttendeeBackground, printerSettings);

        writePdf(file, pdfStream);

        long endTime = System.currentTimeMillis();
        return String.format("Wrote %s attendees to %s in %s ms", attendees.size(), file, endTime - startTime);
    }

    private String writeGuestBadgesToPdf(List<Guest> guests, File file, PrinterSettings printerSettings) throws IOException {
        long startTime = System.currentTimeMillis();
        InputStream pdfStream = badgePrintService.generateGuestPDF(guests, Sides.BOTH, printerSettings);

        writePdf(file, pdfStream);

        long endTime = System.currentTimeMillis();
        return String.format("Wrote %s guests to %s in %s ms", guests.size(), file, endTime - startTime);
    }

    /**
     * Writes bytes from pdf stream to file
     * Returns number of bytes written
     */
    private void writePdf(File file, InputStream pdfStream) throws IOException {
        byte[] media = pdfStream.readAllBytes();

        try(OutputStream os = new FileOutputStream(file)) {
            os.write(media);
        }
    }

    /**
     * Helper method to combine and return attendee lists by badge type
     */
    private List<Attendee> getAttendeeListByType(String type) {
        // Note: Should probably do this comparison in SQL, that would mean modifying
        // findALlByBadgeType() to take multiple badge types. Also excluding revoked
        // memberships in that function would be good
        Comparator<Attendee> compareByName = Comparator
                .comparing(Attendee::getLastName)
                .thenComparing(Attendee::getFirstName);

        switch (type) {
            case "Attendee":
                List<Attendee> weekend = attendeeRepository.findAllByBadgeType(1);
                List<Attendee> militaryWeekend = attendeeRepository.findAllByBadgeType(2);
                List<Attendee> friday = attendeeRepository.findAllByBadgeType(3);
                List<Attendee> saturday = attendeeRepository.findAllByBadgeType(4);
                List<Attendee> sunday = attendeeRepository.findAllByBadgeType(5);

                return Stream.of(weekend, militaryWeekend, friday, saturday, sunday)
                        .flatMap(Collection::stream)
                        .filter(a -> !a.isMembershipRevoked())
                        .sorted(compareByName)
                        .toList();
            case "Vip":
                return attendeeRepository.findAllByBadgeType(6);
            case "Specialty":
                List<Attendee> artist = attendeeRepository.findAllByBadgeType(7);
                List<Attendee> exhibitor = attendeeRepository.findAllByBadgeType(8);
                List<Attendee> smallPress = attendeeRepository.findAllByBadgeType(10);
                List<Attendee> emergingPress = attendeeRepository.findAllByBadgeType(11);
                List<Attendee> standardPress = attendeeRepository.findAllByBadgeType(12);
                List<Attendee> industry = attendeeRepository.findAllByBadgeType(13);
                List<Attendee> panelist = attendeeRepository.findAllByBadgeType(14);

                return Stream.of(artist, exhibitor, smallPress, emergingPress, standardPress, industry, panelist)
                        .flatMap(Collection::stream)
                        .filter(a -> !a.isMembershipRevoked())
                        .sorted(compareByName)
                        .toList();
            default:
                log.error("Attendee type {} not recognized", type);
                return List.of();
        }
    }

    private void setAttendeesPreprinted(List<Attendee> attendees) {
        log.info("Marking {} attendees as preprinted", attendees.size());
        attendees.forEach(attendee -> {
            attendee.setBadgePrePrinted(true);
            attendeeRepository.save(attendee);
        });
    }
}

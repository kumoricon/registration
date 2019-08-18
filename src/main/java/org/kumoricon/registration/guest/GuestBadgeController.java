package org.kumoricon.registration.guest;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.print.formatter.BadgePrintFormatter;
import org.kumoricon.registration.print.formatter.FullBadgePrintFormatter;
import org.kumoricon.registration.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.kumoricon.registration.print.formatter.badgeimage.BadgeCreator;
import org.kumoricon.registration.print.formatter.badgeimage.BadgeCreatorFull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class GuestBadgeController {
    private final GuestRepository guestRepository;

    private static final Logger log = LoggerFactory.getLogger(GuestBadgeController.class);

    public GuestBadgeController(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }



    @RequestMapping(value = "/guests/{guestId}/badge.png")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public ResponseEntity<byte[]> getBadgeImage(@PathVariable Integer guestId) {
        Guest guest = guestRepository.findById(guestId);

        log.info("viewed badge image for {}", guest);
        AttendeeBadgeDTO a = AttendeeBadgeDTO.fromGuest(guest);

        BadgeCreator badgeCreator = new BadgeCreatorFull();

        byte[] media = badgeCreator.createBadge(a);
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/guests/{guestId}/badge.pdf")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public ResponseEntity<byte[]> getBadgePdf(@PathVariable Integer guestId,
                                              @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        Guest guest = guestRepository.findById(guestId);
        log.info("downloaded badge PDF for {}", guest);
        AttendeeBadgeDTO a = AttendeeBadgeDTO.fromGuest(guest);

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(List.of(a), printerSettings.getxOffset(), printerSettings.getyOffset());

        byte[] media = badgePrintFormatter.getStream().readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/guests/allguestbadges.pdf")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public ResponseEntity<byte[]> getAllGuestBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        List<Guest> guests = guestRepository.findAll();

        log.info("downloading all guest badge PDF");

        long start = System.currentTimeMillis();
        List<AttendeeBadgeDTO> badges = new ArrayList<>();
        for (Guest g : guests) {
            badges.add(AttendeeBadgeDTO.fromGuest(g));
        }

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(badges, printerSettings.getxOffset(), printerSettings.getyOffset());

        byte[] media = badgePrintFormatter.getStream().readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        long end = System.currentTimeMillis();
        log.info("generated {} guest badges in {} ms", badges.size(), end-start);
        return responseEntity;
    }


    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setExpires(3);
        return headers;
    }

}
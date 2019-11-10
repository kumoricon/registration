package org.kumoricon.registration.guest;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.staff.BadgeResourceService;
import org.kumoricon.registration.model.staff.StaffBadgeDTO;
import org.kumoricon.registration.print.BadgePrintService;
import org.kumoricon.registration.print.Sides;
import org.kumoricon.registration.print.formatter.BadgePrintFormatter;
import org.kumoricon.registration.print.formatter.StaffBadgePrintFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;


@Controller
public class GuestBadgeController {
    private final GuestRepository guestRepository;
    private final BadgePrintService badgePrintService;
    private final BadgeResourceService badgeResourceService;

    private static final Logger log = LoggerFactory.getLogger(GuestBadgeController.class);

    public GuestBadgeController(GuestRepository guestRepository, BadgePrintService badgePrintService, BadgeResourceService badgeResourceService) {
        this.guestRepository = guestRepository;
        this.badgePrintService = badgePrintService;
        this.badgeResourceService = badgeResourceService;
    }



    @RequestMapping(value = "/guests/{guestId}/badge.png")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public ResponseEntity<byte[]> getBadgeImage(@PathVariable Integer guestId) {
        Guest guest = guestRepository.findById(guestId);

        log.info("viewed badge image for {}", guest);

        byte[] media = badgePrintService.generateGuestImage(List.of(guest));
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/guests/{guestId}/badge.pdf")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public ResponseEntity<byte[]> getBadgePdf(@PathVariable Integer guestId,
                                              @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        Guest guest = guestRepository.findById(guestId);
        log.info("downloaded badge PDF for {}", guest);

        List<StaffBadgeDTO> badgeDTOS = badgePrintService.staffBadgeDTOsFromGuest(List.of(guest));

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        BadgePrintFormatter badgePrintFormatter = new StaffBadgePrintFormatter(badgeDTOS, Sides.BOTH, printerSettings.getxOffset(), printerSettings.getyOffset(), badgeResourceService.getGuestBadgeResource());

        byte[] media = badgePrintFormatter.getStream().readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/guests/allguestbadges.pdf")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public ResponseEntity<byte[]> getAllGuestBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        List<Guest> guests = guestRepository.findAll();

        log.info("downloading all guest badge PDF");

        long start = System.currentTimeMillis();
        List<StaffBadgeDTO> badgeDTOS = badgePrintService.staffBadgeDTOsFromGuest(guests);

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        BadgePrintFormatter badgePrintFormatter = new StaffBadgePrintFormatter(badgeDTOS, Sides.BOTH, printerSettings.getxOffset(), printerSettings.getyOffset(), badgeResourceService.getGuestBadgeResource());

        byte[] media = badgePrintFormatter.getStream().readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        long end = System.currentTimeMillis();
        log.info("generated {} guest badges in {} ms", badgeDTOS.size(), end-start);
        return responseEntity;
    }


    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setExpires(3);
        return headers;
    }

}
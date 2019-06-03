package org.kumoricon.registration.order;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.attendee.*;
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
import java.util.List;


@Controller
public class BadgeImageController {
    private final AttendeeRepository attendeeRepository;
    private final BadgeService badgeService;

    private static final Logger log = LoggerFactory.getLogger(BadgeImageController.class);

    public BadgeImageController(AttendeeRepository attendeeRepository, BadgeService badgeService) {
        this.attendeeRepository = attendeeRepository;
        this.badgeService = badgeService;
    }


    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/badge.png")
    @PreAuthorize("hasAuthority('manage_orders')")
    public ResponseEntity<byte[]> getBadgeImage(@PathVariable Integer orderId,
                                              @PathVariable Integer attendeeId) {
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        log.info("viewed badge image for {}", attendee);
        Badge badge = badgeService.findById(attendee.getBadgeId());
        AttendeeBadgeDTO a = AttendeeBadgeDTO.fromAttendee(attendee, badge);

        BadgeCreator badgeCreator = new BadgeCreatorFull();

        byte[] media = badgeCreator.createBadge(a);
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/badge.pdf")
    @PreAuthorize("hasAuthority('manage_orders')")
    public ResponseEntity<byte[]> getBadgePdf(@PathVariable Integer orderId,
                                              @PathVariable Integer attendeeId,
                                              @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        log.info("downloaded badge PDF for {}", attendee);
        Badge badge = badgeService.findById(attendee.getBadgeId());
        AttendeeBadgeDTO a = AttendeeBadgeDTO.fromAttendee(attendee, badge);

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(List.of(a), printerSettings.getxOffset(), printerSettings.getyOffset());

        byte[] media = badgePrintFormatter.getStream().readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        return responseEntity;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setExpires(3);
        return headers;
    }

}
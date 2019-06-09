package org.kumoricon.registration.utility;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.AttendeeService;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.print.BadgePrintService;
import org.kumoricon.registration.print.formatter.BadgePrintFormatter;
import org.kumoricon.registration.print.formatter.FullBadgePrintFormatter;
import org.kumoricon.registration.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class TestBadgeController {

    private final String[] NAMES_TO_TEST = new String[]{"I have a really long name here and it makes life miserable for text",
            "しん ★", "オリビア • ベルトラン",  "キャサティ-", "2814.5", ":3", "クララ・コアラ", "ミラちゃん",
            ">:(", "ಠ_ಠ", "∆$#", "( ͡° ͜ʖ ͡°)", "ひな", "もんど", "ルイ-ス", "高原・コーゲン"};

    private static final Logger log = LoggerFactory.getLogger(TestBadgeController.class);

    public TestBadgeController() {}

    @RequestMapping(value = "/utility/testbadges.pdf")
//    @PreAuthorize("hasAuthority('manage_orders')")
    public ResponseEntity<byte[]> getTestBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        long start = System.currentTimeMillis();
        log.info("generating test badge pdf");
        List<AttendeeBadgeDTO> attendeeBadgeDTOS = generateAttendees();

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        BadgePrintFormatter badgePrintFormatter = new FullBadgePrintFormatter(attendeeBadgeDTOS, printerSettings.getxOffset(), printerSettings.getyOffset());

        byte[] media = badgePrintFormatter.getStream().readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        log.info("generated test badge PDF in {} ms", System.currentTimeMillis()-start);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);
    }

    private List<AttendeeBadgeDTO> generateAttendees() {
        List<AttendeeBadgeDTO> badgeDTOs = new ArrayList<>();

        for (String name : NAMES_TO_TEST) {
            AttendeeBadgeDTO a = new AttendeeBadgeDTO();
            a.setId(10000);
            a.setFanName(name);
            a.setBadgeTypeText("Name Test");
            a.setBadgeNumber("TS12345");
            badgeDTOs.add(a);
        }

        return badgeDTOs;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setExpires(1);
        return headers;
    }

}
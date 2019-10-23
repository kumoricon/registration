package org.kumoricon.registration.utility;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.badge.BadgeType;
import org.kumoricon.registration.print.BadgePrintService;
import org.kumoricon.registration.print.formatter.badgeimage.AttendeeBadgeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final BadgePrintService badgePrintService;
    private final BadgeService badgeService;

    public TestBadgeController(BadgePrintService badgePrintService, BadgeService badgeService) {
        this.badgePrintService = badgePrintService;
        this.badgeService = badgeService;
    }

    @RequestMapping(value = "/utility/testbadges")
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public String getTillSession(Model model) {
        return "utility/testbadges.html";
    }

    @RequestMapping(value = "/utility/testBadges.pdf")
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public ResponseEntity<byte[]> getTestBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        return generateAttendeePDF(printerCookie, generateTestAttendees(), BadgeType.ATTENDEE);
    }

    @RequestMapping(value = "/utility/attendeeBadges.pdf")
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public ResponseEntity<byte[]> getAllAttendeeBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        return generateAttendeePDF(printerCookie, generateAllAttendeeBadges(BadgeType.ATTENDEE), BadgeType.ATTENDEE);
    }

    @RequestMapping(value = "/utility/specialtyBadges.pdf")
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public ResponseEntity<byte[]> getAllSpecialtyBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        return generateAttendeePDF(printerCookie, generateAllAttendeeBadges(BadgeType.SPECIALTY), BadgeType.SPECIALTY);
    }

    @RequestMapping(value = "/utility/vipBadges.pdf")
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public ResponseEntity<byte[]> getAllVipBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        return generateAttendeePDF(printerCookie, generateAllAttendeeBadges(BadgeType.VIP), BadgeType.VIP);
    }


    private ResponseEntity<byte[]> generateAttendeePDF(String printerCookie, List<AttendeeBadgeDTO> attendeeBadgeDTOS, BadgeType badgeType) throws IOException {
        long start = System.currentTimeMillis();

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        byte[] media = badgePrintService.generateAttendeePDFfromDTO(attendeeBadgeDTOS, printerSettings, badgeType).readAllBytes();
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        log.info("generated test badge PDF in {} ms", System.currentTimeMillis()-start);

        return new ResponseEntity<>(media, headers, HttpStatus.OK);

    }


    private List<AttendeeBadgeDTO> generateAllAttendeeBadges(BadgeType badgeType) {
        List<AttendeeBadgeDTO> badgeDTOS = new ArrayList<>();
        int cnt = 100000;
        for (Badge b : badgeService.findAll()) {
            if (b.getBadgeType() == badgeType) {
                AttendeeBadgeDTO a = new AttendeeBadgeDTO();
                a.setId(cnt++);
                a.setName("Firstname Lastname");
                a.setFanName("Fan Name");
                a.setBadgeNumber("AB12345");
                a.setPronoun("They/Them");
                a.setBadgeTypeText(b.getBadgeTypeText());
                a.setBadgeTypeBackgroundColor(b.getBadgeTypeBackgroundColor());
                a.setAgeStripeText("Adult");
                a.setAgeStripeBackgroundColor("#323E99");
                badgeDTOS.add(a);
            }
        }
        return badgeDTOS;
    }


    private List<AttendeeBadgeDTO> generateTestAttendees() {
        List<AttendeeBadgeDTO> badgeDTOs = new ArrayList<>();

        for (String name : NAMES_TO_TEST) {
            AttendeeBadgeDTO a = new AttendeeBadgeDTO();
            a.setId(10000);
            a.setFanName(name);
            a.setBadgeTypeText("Name Test");
            a.setBadgeNumber("TS12345");
            a.setPronoun("They/Them");
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
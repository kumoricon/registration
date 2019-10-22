package org.kumoricon.registration.utility;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
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

    public TestBadgeController(BadgePrintService badgePrintService) {
        this.badgePrintService = badgePrintService;
    }

    @RequestMapping(value = "/utility/testbadges")
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public String getTillSession(Model model) {
        return "utility/testbadges.html";
    }

    @RequestMapping(value = "/utility/testbadges.pdf")
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public ResponseEntity<byte[]> getTestBadgePdf(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) throws IOException {
        long start = System.currentTimeMillis();
        log.info("generating test badge pdf");

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        byte[] media = badgePrintService.generateAttendeePDFfromDTO(generateAttendees(), printerSettings).readAllBytes();
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
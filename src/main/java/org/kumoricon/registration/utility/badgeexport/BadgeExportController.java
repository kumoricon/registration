package org.kumoricon.registration.utility.badgeexport;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BadgeExportController {
    private static final Logger log = LoggerFactory.getLogger(BadgeExportController.class);

    private final BadgeExportService badgeExportService;
    private final BadgeExport badgeExport;

    public BadgeExportController(BadgeExportService badgeExportService, BadgeExport badgeExport) {
        this.badgeExportService = badgeExportService;
        this.badgeExport = badgeExport;
    }

    @RequestMapping(value = "/utility/exportbadges", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public String exportBadgesGet(Model model) {
        model.addAttribute("badgeexport", badgeExport);

        return "utility/exportbadges";
    }

    @RequestMapping(value = "/utility/exportbadges", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public String exportBadgesPost(@ModelAttribute("badgeexport") BadgeExport badgeExport) {

        this.badgeExport.setType(badgeExport.getType());
        this.badgeExport.setMarkPreprinted(badgeExport.isMarkPreprinted());
        this.badgeExport.setWithAttendeeBackground(badgeExport.isWithAttendeeBackground());
        this.badgeExport.setPath(badgeExport.getPath());
        return "redirect:exportbadges/" + badgeExport.getType().toLowerCase() + "-badges.pdf";
    }

    @RequestMapping(value = "/utility/exportbadges/{type}-badges.pdf")
    @PreAuthorize("hasAuthority('staff_check_in')")
    public ResponseEntity<String> delegateBadgeExport(@CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        log.info("export all {} badge PDF page 1+", badgeExport.getType());

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        badgeExportService.delegateBadgeExport(printerSettings);

        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>("Saved", headers, HttpStatus.OK);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        headers.setExpires(3);
        return headers;
    }
}

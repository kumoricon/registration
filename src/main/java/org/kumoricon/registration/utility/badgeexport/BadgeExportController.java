package org.kumoricon.registration.utility.badgeexport;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BadgeExportController {
    private static final Logger log = LoggerFactory.getLogger(BadgeExportController.class);

    private final BadgeExportService badgeExportService;
    private final Boolean printAttendeeBackgrounds;

    public BadgeExportController(@Value("${badge.printAttendeeBackgrounds}") Boolean printAttendeeBackgrounds,
                                 BadgeExportService badgeExportService) {
        this.badgeExportService = badgeExportService;
        this.printAttendeeBackgrounds = printAttendeeBackgrounds;
    }

    @RequestMapping(value = "/utility/exportbadges", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public String exportBadgesGet(Model model) {
        BadgeExport defaults = new BadgeExport(printAttendeeBackgrounds);
        defaults.setPath("/tmp");
        model.addAttribute("badgeexport", defaults);
        return "utility/exportbadges";
    }

    @RequestMapping(value = "/utility/exportbadges", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('pre_print_badges')")
    public String exportBadgesPost(@ModelAttribute("badgeexport") BadgeExport badgeExport,
                                   @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        log.info("export all {} badge PDF page 1+", badgeExport.getType());

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        badgeExportService.delegateBadgeExport(badgeExport, printerSettings);

        return "redirect:/utility/exportbadges?msg=Exported+badges";
    }
}

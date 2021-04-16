package org.kumoricon.registration.utility;


import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.model.tillsession.TillSessionDTO;
import org.kumoricon.registration.model.tillsession.TillSessionDetailDTO;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.kumoricon.registration.print.ReportPrintService;
import org.kumoricon.registration.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.PrintException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@Controller
public class TillSessionController {
    private final TillSessionService tillSessionService;
    private final UserService userService;
    private final SettingsService settingsService;
    private final ReportPrintService reportService;
    private static final Logger log = LoggerFactory.getLogger(TillSessionController.class);

    public TillSessionController(TillSessionService tillSessionService, UserService userService, SettingsService settingsService, ReportPrintService reportService) {
        this.tillSessionService = tillSessionService;
        this.userService = userService;
        this.settingsService = settingsService;
        this.reportService = reportService;
    }

    @RequestMapping(value = "/utility/till")
    public String getTillSession(Model model,
                                 @CookieValue(value = CookieControllerAdvice.TILL_COOKIE_NAME, required = false) String tillName,
                                 Principal user) {
        User currentUser = userService.findByUsername(user.getName());
        if (currentUser == null) throw new RuntimeException("User not found");

        TillSessionDTO s = tillSessionService.getOpenSessionForUser(currentUser);
        String reportPrinterName = settingsService.getCurrentSettings().getReportPrinterName();
        model.addAttribute("reportPrinterName", reportPrinterName);
        model.addAttribute("tillName", tillName);
        model.addAttribute("tillSession", s);
        return "utility/tillsession";
    }

    @RequestMapping(value = "/utility/till", method = RequestMethod.POST)
    public String closeTill(Model model,
                            Principal user,
                            @RequestParam String action,
                            @RequestParam String tillName,
                            HttpServletResponse response) {

        User currentUser = userService.findByUsername(user.getName());
        if (currentUser == null) throw new RuntimeException("User not found");

        String sanitizedTillName = tillName.trim();

        if (action.equals("Save")) {
            log.info("Setting till to {}", tillName);
            try {
                Cookie cookie = new Cookie(CookieControllerAdvice.TILL_COOKIE_NAME, sanitizedTillName);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
                return "redirect:/utility/till?msg=Set+till+" + sanitizedTillName;

            } catch (Exception e) {
                model.addAttribute("tillName", tillName);
                model.addAttribute("tillSession", tillSessionService.getOpenSessionForUser(currentUser));

                log.error("Error setting till to {}", sanitizedTillName, e);
                model.addAttribute("err", e.getMessage());
                return "utility/tillsession";
            }
        } else if (action.equals("Close Till")) {
            TillSessionDTO s = tillSessionService.getOpenSessionForUser(currentUser);
            int tillSessionId = s.getId();
            tillSessionService.closeSessionForUser(currentUser, tillName);
            String reportPrinterName = settingsService.getCurrentSettings().getReportPrinterName();
            String msg;
            if (reportPrinterName != null && !reportPrinterName.isEmpty()) {
                try {
                    TillSessionDetailDTO s2 = tillSessionService.getTillDetailDTO(tillSessionId);
                    reportService.printTillReport(currentUser.getId(), tillSessionId, reportPrinterName, s2);
                    msg = "Till Session " + tillSessionId + " Closed. Report printed to '" + reportPrinterName + "'.";
                }
                catch(Exception e) {
                    msg = "Till Session " + tillSessionId + " Closed. The report failed to print. The till report must be printed manually.";
                }
            }
            else {
                msg = "Till Session " + tillSessionId + " Closed. No report printer available. The till report must be printed manually.";
            }

            msg = msg.replace(" ", "%20");
            return "redirect:/utility/till?msg=" + msg;
        }

        throw new RuntimeException("Action not specified");
    }
}
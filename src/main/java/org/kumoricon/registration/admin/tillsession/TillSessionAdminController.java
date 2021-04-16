package org.kumoricon.registration.admin.tillsession;

import org.kumoricon.registration.model.tillsession.TillSessionDTO;
import org.kumoricon.registration.model.tillsession.TillSessionDetailDTO;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.kumoricon.registration.print.ReportPrintService;
import org.kumoricon.registration.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.PrintException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class TillSessionAdminController {
    private final TillSessionService tillSessionService;
    private final UserService userService;
    private final ReportPrintService reportService;
    private final SettingsService settingsService;

    private static final Logger log = LoggerFactory.getLogger(TillSessionAdminController.class);

    public TillSessionAdminController(TillSessionService tillSessionService, UserService userService, ReportPrintService reportService, SettingsService settingsService) {
        this.tillSessionService = tillSessionService;
        this.userService = userService;
        this.reportService = reportService;
        this.settingsService = settingsService;
    }

    @RequestMapping(value = "/admin/tillsessions")
    @PreAuthorize("hasAuthority('manage_till_sessions')")
    public String admin(Model model,
                        @RequestParam(value = "filter", defaultValue = "open", required = false) String filter) {

        model.addAttribute("tillSessions", getTillSessions(filter));
        model.addAttribute("filter", filter);
        return "admin/tillsessions";
    }


    @RequestMapping(value = "/admin/tillsessions/{id}")
    @PreAuthorize("hasAuthority('manage_till_sessions')")
    public String viewTillSessionReport(Model model,
                                        @PathVariable Integer id,
                                        @RequestParam(required = false, defaultValue = "false") Boolean showIndividualOrders,
                                        @RequestParam(required = false, defaultValue = "false") Boolean printTillReport,
                                        @RequestParam(required = false, defaultValue = "false") Boolean saveTillReport) throws IOException, PrintException {

        // Handle requests to print a till report
        if (printTillReport != null && printTillReport == true) {
            TillSessionDetailDTO s2 = tillSessionService.getTillDetailDTO(id);
            String reportPrinterName = settingsService.getCurrentSettings().getReportPrinterName();
            if (!s2.isOpen()) {
                reportService.printTillReport(s2.getUserId(), s2.getId(), reportPrinterName, s2);
                model.addAttribute("printTillReport", true);
                model.addAttribute("reportPrinterName", reportPrinterName);
            }
        }
        else {
            model.addAttribute("printTillReport", false);
        }

        // Handle requests to save a till report
        if (saveTillReport != null && saveTillReport == true) {
            TillSessionDetailDTO s2 = tillSessionService.getTillDetailDTO(id);
            if (!s2.isOpen()) {
                String savePath = "/Volumes/Data/Kumoreg/test.pdf";
                reportService.saveTillReport(s2.getUserId(), s2.getId(), savePath, s2);
                model.addAttribute("saveTillReport", true);
                model.addAttribute("savePath", savePath);
            }
        }
        else {
            model.addAttribute("saveTillReport", false);
        }

        model.addAttribute("tillSession", tillSessionService.getTillDetailDTO(id));
        model.addAttribute("showIndividualOrders", showIndividualOrders);

        return "reports/tillsessions-id";
    }


    @RequestMapping(value = "/admin/tillsessions", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('manage_till_sessions')")
    public String closeSession(Model model,
                               @RequestParam Integer sessionId,
                               @RequestParam(value="filter", defaultValue = "open", required = false) String filter,
                               Principal principal) {

        User user = userService.findByUsername(principal.getName());
        log.info("{} closing till session {}", user, sessionId);

        tillSessionService.closeSession(sessionId);

        model.addAttribute("msg", "Session " + sessionId + " closed");
        model.addAttribute("filter", filter);
        model.addAttribute("tillSessions", getTillSessions(filter));
        return "admin/tillsessions";
    }


    private List<TillSessionDTO> getTillSessions(String filter) {
        if ("open".equals(filter)) {
            return tillSessionService.getOpenTillSessionDTOs();
        } else {
            return tillSessionService.getAllTillSessionDTOs();
        }
    }

}

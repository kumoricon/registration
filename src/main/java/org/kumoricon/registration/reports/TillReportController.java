package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.tillsession.TillSessionDetailDTO;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.UserService;
import org.kumoricon.registration.print.ReportPrintService;
import org.kumoricon.registration.settings.SettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.PrintException;
import java.io.IOException;

@Controller
public class TillReportController {
    private final TillSessionService tillSessionService;
    private final ReportPrintService reportService;
    private final SettingsService settingsService;

    public TillReportController(TillSessionService tillSessionService, ReportPrintService reportService, SettingsService settingsService) {
        this.tillSessionService = tillSessionService;
        this.reportService = reportService;
        this.settingsService = settingsService;
    }

    @RequestMapping(value = "/reports/tillsessions")
    @PreAuthorize("hasAuthority('view_till_report')")
    public String till(Model model) {
        try {
            model.addAttribute("tills", tillSessionService.getAllTillSessionDTOs());
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }
        return "reports/tillsessions";
    }

    @RequestMapping(value = "/reports/tillsessions/{id}")
    @PreAuthorize("hasAuthority('view_till_report')")
    public String tillId(Model model,
                         @PathVariable Integer id,
                         @RequestParam(required = false, defaultValue = "false") Boolean showIndividualOrders,
                         @RequestParam(required = false, defaultValue = "false") Boolean printTillReport) throws IOException, PrintException {

        System.out.println("before print till report");
        if (printTillReport != null && printTillReport == true) {
            System.out.println("print till report");
            TillSessionDetailDTO s2 = tillSessionService.getTillDetailDTO(id);
            String reportPrinterName = settingsService.getCurrentSettings().getReportPrinterName();
            reportService.printTillReport(s2.getUserId(), s2.getId(), reportPrinterName, s2);
            model.addAttribute("printTillReport", true);
            model.addAttribute("reportPrinterName", reportPrinterName);
        }
        else {
            model.addAttribute("printTillReport", false);
        }

        model.addAttribute("tillSession", tillSessionService.getTillDetailDTO(id));
        model.addAttribute("showIndividualOrders", showIndividualOrders);

        return "reports/tillsessions-id";
    }

}
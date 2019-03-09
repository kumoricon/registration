package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TillReportController {
    private final TillSessionService tillSessionService;

    @Autowired
    public TillReportController(TillSessionService tillSessionService) {
        this.tillSessionService = tillSessionService;
    }

    @RequestMapping(value = "/reports/tillsessions")
    @PreAuthorize("hasAuthority('view_till_report')")
    public String till(Model model,
                        @RequestParam(required=false) String err,
                        @RequestParam(required=false) String msg) {
        try {
            model.addAttribute("tills", tillSessionService.getAllSessions());
            model.addAttribute("err", err);
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        model.addAttribute("msg", msg);

        return "reports/tillsessions";
    }
}
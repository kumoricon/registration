package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TillReportController {
    private final TillSessionService tillSessionService;

    public TillReportController(TillSessionService tillSessionService) {
        this.tillSessionService = tillSessionService;
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
                         @RequestParam(required = false, defaultValue = "false") Boolean showIndividualOrders) {

        model.addAttribute("tillSession", tillSessionService.getTillDetailDTO(id));
        model.addAttribute("showIndividualOrders", showIndividualOrders);

        return "reports/tillsessions-id";
    }

}
package org.kumoricon.registration.reports;

import org.kumoricon.registration.helpers.DateTimeService;
import org.kumoricon.registration.model.tillsession.TillSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class TillReportController {
    private final TillSessionRepository tillSessionRepository;

    @Autowired
    public TillReportController(TillSessionRepository tillSessionRepository) {
        this.tillSessionRepository = tillSessionRepository;
    }

    @RequestMapping(value = "/reports/tillsessions")
    @PreAuthorize("hasAuthority('view_attendance_report')")
    public String admin(Model model) {
        model.addAttribute("data", tillSessionRepository.findAllOrderByEnd());
        return "reports/tillsessions";
    }
}

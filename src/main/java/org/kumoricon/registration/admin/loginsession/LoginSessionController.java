package org.kumoricon.registration.admin.loginsession;

import org.kumoricon.registration.helpers.DateTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LoginSessionController {
    private final LoginRepository loginRepository;
    private final DateTimeService dateTimeService;

    @Autowired
    public LoginSessionController(LoginRepository loginRepository, DateTimeService dateTimeService) {
        this.loginRepository = loginRepository;
        this.dateTimeService = dateTimeService;
    }

    @RequestMapping(value = "/admin/loginsessions")
    @PreAuthorize("hasAuthority('view_active_sessions')")
    public String admin(Model model) {
        List<SessionInfoDTO> logins = loginRepository.findAll();

        model.addAttribute("loginSessions", logins);
        model.addAttribute("fmt", dateTimeService);
        return "admin/loginsessions";
    }
}

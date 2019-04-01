package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class StaffReportController {
    private final UserRepository userRepository;

    @Autowired
    public StaffReportController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @RequestMapping(value = "/reports/staff")
    @PreAuthorize("hasAuthority('view_staff_report')")
    public String staff(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("staff", users);
        return "reports/staff";
    }
}
package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class StaffReportController {
    private final StaffRepository staffRepository;

    @Autowired
    public StaffReportController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @RequestMapping(value = "/reports/staff")
    @PreAuthorize("hasAuthority('manage_users')")
    public String listUsers(Model model, @RequestParam(required = false) String err, @RequestParam(required=false) String msg) {
        List<User> users = staffRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "admin/users";
    }
}

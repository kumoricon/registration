package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class RolesReportController {
    private final RoleRepository roleRepository;

    @Autowired
    public RolesReportController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @RequestMapping(value = "/reports/roles")
    @PreAuthorize("hasAuthority('view_role_report')")
    public String listRoles(Model model) {
        model.addAttribute("data", roleRepository.findAllRoles());
        return "reports/roles";
    }

}

package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RolesReportController {
    private final RoleRepository roleRepository;

    @Autowired
    public RolesReportController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @RequestMapping(value = "/reports/roles")
    @PreAuthorize("hasAuthority('view_role_report')")
    public String roles(Model model,
                        @RequestParam(required = false) String err,
                        @RequestParam(required=false) String msg) {
        try {
            model.addAttribute("roles", roleRepository.findAllRoles());
            model.addAttribute("err", err);
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        model.addAttribute("msg", msg);

        return "reports/roles";
    }

}

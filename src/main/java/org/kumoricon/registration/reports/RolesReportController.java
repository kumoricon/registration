package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.role.Right;
import org.kumoricon.registration.model.role.RoleDTO;
import org.kumoricon.registration.model.role.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RolesReportController {
    private final RoleService roleService;

    public RolesReportController(RoleService roleService) {
        this.roleService = roleService;
    }

    @RequestMapping(value = "/reports/roles")
    @PreAuthorize("hasAuthority('view_role_report')")
    public String roles(Model model) {
        try {
            List<RoleDTO> roles = roleService.findAll();
            List<String> columns = new ArrayList<>();
            columns.add("Right");
            for (RoleDTO roleDTO : roles) {
                columns.add(roleDTO.getName());
            }

            List<List<String>> rows = new ArrayList<>();
            for (Right right : roleService.findAllRights()) {
                List<String> row = new ArrayList<>();
                row.add(right.getName());
                for (RoleDTO roleDTO : roles) {
                    if (roleDTO.hasRightId(right.getId())) {
                        row.add("X");
                    } else {
                        row.add(" ");
                    }
                }
                rows.add(row);
            }

            model.addAttribute("columns", columns);
            model.addAttribute("rows", rows);
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        return "reports/roles";
    }

}

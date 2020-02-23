package org.kumoricon.registration.admin.roles;

import org.kumoricon.registration.model.role.RightRepository;
import org.kumoricon.registration.model.role.Role;
import org.kumoricon.registration.model.role.RoleRepository;
import org.kumoricon.registration.model.role.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RolesController {
    private final RoleRepository roleRepository;
    private final RoleValidator roleValidator;
    private final RightRepository rightRepository;

    @Autowired
    public RolesController(RoleRepository roleRepository, RoleValidator roleValidator, RightRepository rightRepository) {
        this.roleRepository = roleRepository;
        this.roleValidator = roleValidator;
        this.rightRepository = rightRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(roleValidator);
    }

    @RequestMapping(value = "/admin/roles")
    @PreAuthorize("hasAuthority('manage_roles')")
    public String listRoles(Model model) {
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("roles", roles);
        return "admin/roles";
    }

    @RequestMapping(value = "/admin/roles/{roleId}")
    @PreAuthorize("hasAuthority('manage_roles')")
    public String editRole(@PathVariable String roleId, final Model model) {
        Role role;
        if (roleId.toLowerCase().equals("new")) {
            role = new Role();
        } else {
            role = roleRepository.findById(Integer.parseInt(roleId));
            if (role == null) {
                model.addAttribute("err", "Error: Role " + roleId + " not found");
            }
        }

        model.addAttribute("role", role);
        model.addAttribute("allRights", rightRepository.findAll());

        return "admin/roles-roleid";
    }


    @RequestMapping("/admin/roles/save")
    @PreAuthorize("hasAuthority('manage_roles')")
    public String saveRole(@ModelAttribute @Validated final Role role,
                           final BindingResult bindingResult, final Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRights", rightRepository.findAll());
            return "admin/roles-roleid";
        }

        try {
            role.setId(roleRepository.save(role));
            rightRepository.saveRightsForRole(role);
        } catch (Exception ex) {
            bindingResult.addError(new ObjectError("Role", ex.getMessage()));
            model.addAttribute("allRights", rightRepository.findAll());
            return "admin/roles-roleid";
        }

        return "redirect:/admin/roles?msg=Saved%20" + role.getName();
    }
}

package org.kumoricon.registration.settings;

import org.kumoricon.registration.model.role.RoleRepository;
import org.kumoricon.registration.model.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SettingsController {
    private final SettingsService settingsService;
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @RequestMapping(value = "/admin/settings")
    @PreAuthorize("hasAuthority('manage_settings')")
    public String listSettings(final Model model) {
        model.addAttribute("settings", settingsService.getCurrentSettings());
        return "admin/settings";
    }
}

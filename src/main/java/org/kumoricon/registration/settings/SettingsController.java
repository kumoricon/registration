package org.kumoricon.registration.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SettingsController {
    private final SettingsService settingsService;
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @RequestMapping(value = "/admin/settings")
    @PreAuthorize("hasAuthority('manage_settings')")
    public String listSettings(final Model model) {
        model.addAttribute("settings", settingsService.getCurrentSettings());
        return "admin/settings";
    }

    @RequestMapping(value = "/admin/settings/toggle", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('manage_settings')")
    public String toggleSetting(@RequestParam String name,
                                @RequestParam String value) {

        log.info("set {} to {}", name, value);
        switch (name) {
            case "enablePrinting" -> settingsService.setPrintingEnabled(Boolean.parseBoolean(value));
            case "trainingMode" -> settingsService.setTrainingMode(Boolean.parseBoolean(value));
            case "reportPrinterName" -> settingsService.setReportPrinterName(value);
            case "requireStaffPhoto" -> settingsService.setRequireStaffPhoto(Boolean.parseBoolean(value));
            case "forcePasswordChange" -> settingsService.setForcePasswordChange(Boolean.parseBoolean(value));
            case "defaultPassword" -> settingsService.setDefaultPassword(value);
        }
        return "redirect:/admin/settings";
    }
}

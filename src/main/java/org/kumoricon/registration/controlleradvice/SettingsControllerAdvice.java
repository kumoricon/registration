package org.kumoricon.registration.controlleradvice;

import org.kumoricon.registration.settings.SettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Responsible for passing global configuration options to the Thymeleaf templates
 */
@ControllerAdvice
public class SettingsControllerAdvice {
    private final SettingsService settingsService;

    public SettingsControllerAdvice(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @ModelAttribute("trainingMode")
    public Boolean trainingMode() {
        return settingsService.getCurrentSettings().getTrainingMode();
    }

}

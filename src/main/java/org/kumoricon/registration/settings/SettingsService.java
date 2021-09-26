package org.kumoricon.registration.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * This service provides configuration options that can be set at run time. Default values may
 * be loaded from the configuration file (usually `application.properties`), but if the settings
 * are also in the database the values in the database will be used.
 *
 * Settings are cached in memory (in an immutable object) to save a database round trip
 */
@Service
public class SettingsService {
    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private final SettingsRepository settingsRepository;
    private Settings currentSettings;

    // Default startup values from config file; will be overwritten by values stored in the database
    @Value("${kumoreg.printing.enablePrintingFromServer}")
    private Boolean enablePrintingFromServerDefault;
    @Value("${kumoreg.trainingMode}")
    private Boolean trainingModeDefault;
    @Value("${staff.requirePhoto}")
    private Boolean requireStaffPhotoDefault;
    @Value("${kumoreg.defaultPassword}")
    private String defaultPasswordDefault;
    @Value("${kumoreg.forcePasswordChange}")
    private Boolean forcePasswordChangeDefault;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Settings getCurrentSettings() {
        return currentSettings;
    }

    @PostConstruct
    public void init() {
        log.info("Loading settings from database...");
        Settings.Builder settings = defaultSettingsBuilder();
        settingsRepository.loadSettingsInto(settings);
        currentSettings = settings.build();
    }

    private Settings.Builder defaultSettingsBuilder() {
        Settings.Builder builder = new Settings.Builder();
        builder.setTrainingMode(trainingModeDefault);
        builder.setEnablePrinting(enablePrintingFromServerDefault);
        builder.setReportPrinterName(null);
        builder.setRequireStaffPhoto(requireStaffPhotoDefault);
        builder.setDefaultPassword(defaultPasswordDefault);
        builder.setForcePasswordChange(forcePasswordChangeDefault);
        builder.setUpdated(System.currentTimeMillis());
        return builder;
    }

    public void setPrintingEnabled(Boolean value) {
        settingsRepository.upsertSetting(SettingsRepository.ENABLE_PRINTING, value.toString());
        this.currentSettings = new Settings.Builder(currentSettings).setEnablePrinting(value).build();
    }

    public void setTrainingMode(Boolean value) {
        settingsRepository.upsertSetting(SettingsRepository.TRAINING_MODE, value.toString());
        this.currentSettings = new Settings.Builder(currentSettings).setTrainingMode(value).build();
    }

    public void setReportPrinterName(String printerName) {
        settingsRepository.upsertSetting(SettingsRepository.REPORT_PRINTER_NAME, printerName);
        this.currentSettings = new Settings.Builder(currentSettings).setReportPrinterName(printerName).build();
    }

    public void setRequireStaffPhoto(Boolean value) {
        settingsRepository.upsertSetting(SettingsRepository.REQUIRE_STAFF_PHOTO, value.toString());
        this.currentSettings = new Settings.Builder(currentSettings).setRequireStaffPhoto(value).build();
    }

    public void setDefaultPassword(String value) {
        settingsRepository.upsertSetting(SettingsRepository.DEFAULT_PASSWORD, value);
        this.currentSettings = new Settings.Builder(currentSettings).setDefaultPassword(value).build();
    }

    public void setForcePasswordChange(Boolean value) {
        settingsRepository.upsertSetting(SettingsRepository.FORCE_PASSWORD_CHANGE, value.toString());
        this.currentSettings = new Settings.Builder(currentSettings).setForcePasswordChange(value).build();
    }
}

package org.kumoricon.registration.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SettingsService {
    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private final SettingsRepository settingsRepository;
    private Settings currentSettings;

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
        return new Settings.Builder()
                .setTrainingMode(false)
                .setEnablePrinting(true)
                .setReportPrinterName(null)
                .setUpdated(System.currentTimeMillis());
    }

    public void setPrintingEnabled(Boolean value) {
        this.currentSettings = new Settings.Builder(currentSettings).setEnablePrinting(value).build();
        settingsRepository.upsertSetting(SettingsRepository.ENABLE_PRINTING, value.toString());
    }

    public void setTrainingMode(Boolean value) {
        this.currentSettings = new Settings.Builder(currentSettings).setTrainingMode(value).build();
        settingsRepository.upsertSetting(SettingsRepository.TRAINING_MODE, value.toString());
    }

    public void setReportPrinterName(String printerName) {
        this.currentSettings = new Settings.Builder(currentSettings).setReportPrinterName(printerName).build();
        settingsRepository.upsertSetting(SettingsRepository.REPORT_PRINTER_NAME, printerName);
    }
}

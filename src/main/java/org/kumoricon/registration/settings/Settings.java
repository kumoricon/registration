package org.kumoricon.registration.settings;

/**
 * Stores all runtime settings
 */
public class Settings {
    private final Long updated;
    private final Boolean enablePrinting;
    private final String reportPrinterName;
    private final Boolean trainingMode;
    private final Boolean requireStaffPhoto;
    private final Boolean requireStaffSignature;
    private final String defaultPassword;
    private final Boolean forcePasswordChange;

    private Settings(Builder builder) {
        updated = builder.updated;
        enablePrinting = builder.enablePrinting;
        reportPrinterName = builder.reportPrinterName;
        trainingMode = builder.trainingMode;
        requireStaffPhoto = builder.requireStaffPhoto;
        requireStaffSignature = builder.requireStaffSignature;
        defaultPassword = builder.defaultPassword;
        forcePasswordChange = builder.forcePasswordChange;
    }

    public Long getUpdated() {
        return updated;
    }
    public String getReportPrinterName() {
        return reportPrinterName;
    }
    public Boolean getEnablePrinting() {
        return enablePrinting;
    }
    public Boolean getTrainingMode() { return trainingMode; }
    public Boolean getRequireStaffPhoto() { return requireStaffPhoto; }
    public Boolean getRequireStaffSignature() { return requireStaffSignature; }
    public String getDefaultPassword() { return defaultPassword; }
    public Boolean getForcePasswordChange() { return forcePasswordChange; }

    public static final class Builder {
        private Long updated;
        private Boolean enablePrinting;
        private String reportPrinterName;
        private Boolean trainingMode;
        private Boolean requireStaffPhoto;
        private Boolean requireStaffSignature;
        private String defaultPassword;
        private Boolean forcePasswordChange;

        public Builder() {
        }

        public Builder(Settings copy) {
            this.updated = copy.getUpdated();
            this.enablePrinting = copy.getEnablePrinting();
            this.reportPrinterName = copy.getReportPrinterName();
            this.trainingMode = copy.getTrainingMode();
            this.requireStaffPhoto = copy.getRequireStaffPhoto();
            this.requireStaffSignature = copy.getRequireStaffSignature();
            this.defaultPassword = copy.getDefaultPassword();
            this.forcePasswordChange = copy.getForcePasswordChange();
        }

        public Builder setUpdated(Long updated) {
            this.updated = updated;
            return this;
        }

        public Builder setEnablePrinting(Boolean enablePrinting) {
            this.enablePrinting = enablePrinting;
            return this;
        }

        public Builder setReportPrinterName(String reportPrinterName) {
            this.reportPrinterName = reportPrinterName;
            return this;
        }

        public Builder setTrainingMode(Boolean trainingMode) {
            this.trainingMode = trainingMode;
            return this;
        }

        public Builder setRequireStaffPhoto(Boolean requireStaffPhoto) {
            this.requireStaffPhoto = requireStaffPhoto;
            return this;
        }

        public Builder setRequireStaffSignature(Boolean requireStaffSignature) {
            this.requireStaffSignature = requireStaffSignature;
            return this;
        }

        public Builder setDefaultPassword(String defaultPassword) {
            assert defaultPassword != null;
            this.defaultPassword = defaultPassword;
            return this;
        }

        public Builder setForcePasswordChange(Boolean forcePasswordChange) {
            this.forcePasswordChange = forcePasswordChange;
            return this;
        }

        public Settings build() {
            return new Settings(this);
        }
    }
}

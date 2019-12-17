package org.kumoricon.registration.settings;

/**
 * Stores all runtime settings
 */
public class Settings {
    private final Long updated;
    private final Boolean enablePrinting;
    private final String reportPrinterName;
    private final Boolean trainingMode;

    private Settings(Builder builder) {
        updated = builder.updated;
        enablePrinting = builder.enablePrinting;
        reportPrinterName = builder.reportPrinterName;
        trainingMode = builder.trainingMode;
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



    public static final class Builder {
        private Long updated;
        private Boolean enablePrinting;
        private String reportPrinterName;
        private Boolean trainingMode;

        public Builder() {
        }

        public Builder(Settings copy) {
            this.updated = copy.getUpdated();
            this.enablePrinting = copy.getEnablePrinting();
            this.reportPrinterName = copy.getReportPrinterName();
            this.trainingMode = copy.getTrainingMode();
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

        public Settings build() {
            return new Settings(this);
        }
    }
}

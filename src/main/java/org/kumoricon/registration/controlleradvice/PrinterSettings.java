package org.kumoricon.registration.controlleradvice;

/**
 * Represents printer settings for a specific client - printer name, x and y offsets. Generally stored in a
 * cookie, see CookieControllerAdvice.java. It is also responsible for serializing to/from cookie values.
 */
public class PrinterSettings {
    private String printerName;
    private Integer xOffset;
    private Integer yOffset;

    public PrinterSettings(String printerName, Integer xOffset, Integer yOffset) {
        if (printerName != null && printerName.trim().isEmpty()) {
            this.printerName = null;
        } else {
            this.printerName = printerName;
        }
        this.xOffset = xOffset == null ? Integer.valueOf(0) : xOffset;
        this.yOffset = yOffset == null ? Integer.valueOf(0) : yOffset;
    }

    public String asCookieValue() {
        String name = printerName == null ? "" : printerName;
        return name + "|" + this.xOffset + "|" + this.yOffset;
    }

    public String getPrinterName() { return printerName; }

    /**
     * @return Horizontal offset in points (1/72 inch). Positive numbers move badge to the right on the page.
     */
    public Integer getxOffset() { return xOffset; }

    /**
     * @return Vertical offset in points (1/72 inch). Positive numbers move badge down on the page.
     */
    public Integer getyOffset() { return yOffset; }

    public static PrinterSettings fromCookieValue(String value) {
        if (value == null) {
            return new PrinterSettings(null, 0, 0);
        }

        String[] values = value.split("\\|");
        if (values.length != 3) {
            throw new RuntimeException("Bad printer cookie value " + value);
        }

        try {
            return new PrinterSettings(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing intger from " + value);
        }
    }

    @Override
    public String toString() {
        return "PrinterSettings{" +
                "printerName='" + printerName + '\'' +
                ", xOffset=" + xOffset +
                ", yOffset=" + yOffset +
                '}';
    }
}

package org.kumoricon.registration.model.staff;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;

public class BadgeResource {
    private final PDDocument background;
    private final Font plainFont;
    private final Font boldFont;

    public BadgeResource(PDDocument background, Font boldFont, Font plainFont) {
        this.background = background;
        this.boldFont = boldFont;
        this.plainFont = plainFont;
    }

    public PDDocument getBackground() {
        return background;
    }

    public Font getPlainFont() {
        return plainFont;
    }

    public Font getBoldFont() {
        return boldFont;
    }
}

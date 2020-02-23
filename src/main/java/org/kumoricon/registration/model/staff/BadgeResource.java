package org.kumoricon.registration.model.staff;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;

public class BadgeResource {
    private final PDDocument background;
    private final Font badgeFont;
    private final Font nameFont;

    public BadgeResource(PDDocument background, Font badgeFont, Font nameFont) {
        this.background = background;
        this.badgeFont = badgeFont;
        this.nameFont = nameFont;
    }

    public PDDocument getBackground() {
        return background;
    }

    public Font getBadgeFont() {
        return badgeFont;
    }

    public Font getNameFont() {
        return nameFont;
    }
}

package org.kumoricon.registration.model.staff;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;

public class BadgeResource {
    private PDDocument background;
    private Font badgeFont;
    private Font nameFont;

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

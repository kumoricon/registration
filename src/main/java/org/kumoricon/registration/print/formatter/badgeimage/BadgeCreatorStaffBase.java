package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class BadgeCreatorStaffBase {
    static final int DPI = 150;
    static final int BADGE_WIDTH = (int) 4.25*DPI;
    static final int BADGE_HEIGHT = (int) 6.25*DPI;

    Font badgeFont;
    Font nameFont;

    public BadgeCreatorStaffBase(Font badgeFont, Font nameFont) {
        this.badgeFont = badgeFont;
        this.nameFont = nameFont;
    }

    void drawBadgeAgeImage(BadgeImage b, StaffBadgeDTO staff) {
        Rectangle badgeImageLocation = new Rectangle(110, 145, 79, 205);
        b.drawStretchedImage(staff.getAgeImage(), badgeImageLocation);
    }


    void drawBadgeTypeStripe(BadgeImage b, StaffBadgeDTO staff) {
        // Guests don't have a department color stripe, but staff do
        if (staff != null && staff.getDepartmentBackgroundColor() != null) {
            Color bgColor = Color.decode(staff.getDepartmentBackgroundColor());
            Color fgColor = BadgeImage.getInverseColor(bgColor);

            Rectangle positionsBackground = new Rectangle(0, 127, 107, 788);
            b.fillRect(positionsBackground, bgColor);

            Rectangle textBounds = new Rectangle(22, 155, 88, 725);
//            b.fillRect(textBounds, Color.CYAN);
            b.drawRotatedCenteredStrings(staff.getPositions(), textBounds, badgeFont, fgColor, true);
        }
    }

    void drawLargeName(BadgeImage b, StaffBadgeDTO staff) {
        String[] name = new String[] {staff.getFirstName(), staff.getLastName()};
        Rectangle largeNameBg = new Rectangle(125, 700, 325, 150);
//        b.fillRect(largeNameBg, Color.ORANGE);
        b.drawCenteredStrings(name, largeNameBg, nameFont, Color.BLACK);
    }

    void drawPronouns(BadgeImage b, StaffBadgeDTO staff) {
        if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isBlank()) {
            Rectangle background = new Rectangle(460, 815, 100, 50);
//            b.fillRect(background, Color.RED);
            b.drawStretchedCenteredString(staff.getPreferredPronoun(), background, badgeFont, Color.BLACK);
        }
    }
}

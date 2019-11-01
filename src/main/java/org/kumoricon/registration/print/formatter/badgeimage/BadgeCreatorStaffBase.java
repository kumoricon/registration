package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import java.awt.*;
import java.util.ArrayList;

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
        // Staff don't have fan names, but guests do, so add that to the list if it's not null.
        // Also, some guests apparently don't have a last name entered.
        String[] names = buildNameList(staff.getFanName(), staff.getFirstName(), staff.getLastName());
        Rectangle largeNameBg = new Rectangle(125, 700, 325, 150);
//        b.fillRect(largeNameBg, Color.ORANGE);
        b.drawCenteredStrings(names, largeNameBg, nameFont, Color.BLACK);
    }

    void drawPronouns(BadgeImage b, StaffBadgeDTO staff) {
        if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isBlank()) {
            Rectangle background = new Rectangle(450, 815, 120, 70);
//            b.fillRect(background, Color.RED);
            b.drawStretchedCenteredString(staff.getPreferredPronoun(), background, nameFont, Color.BLACK, 0);
        }
    }

    /**
     * Builds an array of Strings from the input strings, excluding any that are null or blank
     * @param strings
     * @return
     */
    String[] buildNameList(String...strings) {
        java.util.List<String> names = new ArrayList<>();
        for (String s : strings) {
            if (s != null && !s.isBlank()) {
                names.add(s);
            }
        }

        String[] output = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            output[i] = names.get(i);
        }
        return output;
    }
}

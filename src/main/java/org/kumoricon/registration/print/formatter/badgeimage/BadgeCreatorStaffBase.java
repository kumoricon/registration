package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import java.awt.*;
import java.util.ArrayList;

class BadgeCreatorStaffBase {
    static final int DPI = 300;
    static final int BADGE_WIDTH = (int) 4.25*DPI;
    static final int BADGE_HEIGHT = (int) 6.25*DPI;

    @SuppressWarnings("WeakerAccess")
    final
    Font badgeFont;
    @SuppressWarnings("WeakerAccess")
    final
    Font nameFont;

    public BadgeCreatorStaffBase(Font badgeFont, Font nameFont) {
        this.badgeFont = badgeFont;
        this.nameFont = nameFont;
    }

    void drawBadgeAgeImage(BadgeImage b, StaffBadgeDTO staff) {
        Rectangle badgeImageLocation = new Rectangle(225, 291, 155, 406);
        b.drawStretchedImage(staff.getAgeImage(), badgeImageLocation);
    }


    void drawBadgeTypeStripe(BadgeImage b, StaffBadgeDTO staff) {
        // Guests don't have a department color stripe, but staff do
        Color fgColor = Color.BLACK;
        if (staff != null && staff.getDepartmentBackgroundColor() != null) {
            Color bgColor = Color.decode(staff.getDepartmentBackgroundColor());
            fgColor = BadgeImage.getInverseColor(bgColor);

            Rectangle positionsBackground = new Rectangle(0, 254, 214, 1576);
            b.fillRect(positionsBackground, bgColor);
        }

        if (staff != null && staff.getPositions() != null) {
            Rectangle textBounds = new Rectangle(44, 310, 176, 1430);
//            b.fillRect(textBounds, Color.CYAN);
            b.drawRotatedCenteredStrings(staff.getPositions(), textBounds, badgeFont, fgColor, true);
        }
    }

    void drawLargeName(BadgeImage b, StaffBadgeDTO staff) {
        // Staff don't have fan names, but guests do, so add that to the list if it's not null.
        // Also, some guests apparently don't have a last name entered.
        String[] names = buildNameList(staff.getFanName(), staff.getFirstName(), staff.getLastName());
        Rectangle largeNameBg = new Rectangle(250, 1400, 650, 300);
//        b.fillRect(largeNameBg, Color.ORANGE);
        b.drawCenteredStrings(names, largeNameBg, nameFont, Color.BLACK);
    }

    void drawPronouns(BadgeImage b, StaffBadgeDTO staff) {
        if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isBlank()) {
            Rectangle background = new Rectangle(900, 1630, 240, 140);
//            b.fillRect(background, Color.RED);
            b.drawStretchedCenteredString(staff.getPreferredPronoun(), background, nameFont, Color.BLACK, 0);
        }
    }

    /**
     * Builds an array of Strings from the input strings, excluding any that are null or blank
     * @param strings Input Strings
     * @return Array
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

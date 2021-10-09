package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import java.awt.*;
import java.util.ArrayList;

class BadgeCreatorStaffBase {
    static final int DPI = 300;
    static final int BADGE_WIDTH = (int) 4.25*DPI;
    static final int BADGE_HEIGHT = (int) 6.25*DPI;

    @SuppressWarnings("WeakerAccess")
    final Font boldFont;
    @SuppressWarnings("WeakerAccess")
    final Font plainFont;

    public BadgeCreatorStaffBase(Font boldFont, Font plainFont) {
        this.boldFont = boldFont;
        this.plainFont = plainFont;
    }

    void drawBadgeAgeImage(BadgeImage b, StaffBadgeDTO staff) {
        Rectangle badgeImageLocation = new Rectangle(265, 311, 155, 406);
        b.drawStretchedImage(staff.getAgeImage(), badgeImageLocation);
    }


    void drawBadgeTypeStripe(BadgeImage b, StaffBadgeDTO staff) {
        // Guests don't have a department color stripe, but staff do
        Color fgColor = Color.BLACK;
        if (staff != null && staff.getDepartmentBackgroundColor() != null && !staff.getDepartmentBackgroundColor().isBlank()) {
            Color bgColor = Color.decode(staff.getDepartmentBackgroundColor());
            fgColor = BadgeImage.getInverseColor(bgColor);

            Rectangle positionsBackground = new Rectangle(0, 280, 238, 1576);
            b.fillRect(positionsBackground, bgColor);
        }

        if (staff != null && staff.getPositions() != null) {
            Rectangle textBounds = new Rectangle(54, 290, 176, 1410);
//            b.fillRect(textBounds, Color.CYAN);
            b.drawRotatedCenteredStrings(staff.getPositions(), textBounds, boldFont, fgColor, true);
        }
    }

    void drawLargeName(BadgeImage b, StaffBadgeDTO staff) {
        // Staff don't have fan names, but guests do, so add that to the list if it's not null.
        // Also, some guests apparently don't have a last name entered.
        String[] names = buildNameList(staff.getFanName(), staff.getFirstName(), staff.getLastName());
        Rectangle largeNameBg = new Rectangle(280, 1325, 850, 300);
//        b.fillRect(largeNameBg, Color.ORANGE);
        b.drawCenteredStrings(names, largeNameBg, boldFont, Color.WHITE);
    }

    void drawPronouns(BadgeImage b, StaffBadgeDTO staff) {
        if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isBlank()) {
            Rectangle background = new Rectangle(700, 1620, 460, 150);
//            b.fillRect(background, Color.RED);
            b.drawStretchedCenteredString(staff.getPreferredPronoun(), background, plainFont, Color.WHITE, 1);
        }
    }

    void drawBadgeNumber(BadgeImage b, StaffBadgeDTO staff) {
        Color fgColor = Color.BLACK;
        if (staff != null && staff.getDepartmentBackgroundColor() != null && !staff.getDepartmentBackgroundColor().isBlank()) {
            Color bgColor = Color.decode(staff.getDepartmentBackgroundColor());
            fgColor = BadgeImage.getInverseColor(bgColor);
        }

        if (staff.getBadgeNumber() != null && !staff.getBadgeNumber().isBlank()) {
            Rectangle background = new Rectangle(40, 1690, 180, 50);
//            b.fillRect(background, Color.GREEN);
            b.drawStretchedCenteredString(staff.getBadgeNumber(), background, plainFont, fgColor, 0);
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

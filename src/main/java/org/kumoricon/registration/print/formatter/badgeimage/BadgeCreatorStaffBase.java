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
//        Rectangle badgeImageLocation = new Rectangle(250, 290, 165, 413);
        Rectangle badgeImageLocation = new Rectangle(260, 292, 165, 413);
//        b.fillRect(badgeImageLocation, Color.RED);
        b.drawImage(staff.getAgeImage(), badgeImageLocation);
    }


    void drawBadgeTypeStripe(BadgeImage b, StaffBadgeDTO staff) {
        // Guests don't have a department color stripe, but staff do
        Color fgColor = Color.BLACK;
        if (staff != null && staff.getDepartmentBackgroundColor() != null && !staff.getDepartmentBackgroundColor().isBlank()) {
            Color bgColor = Color.decode(staff.getDepartmentBackgroundColor());
            fgColor = BadgeImage.getInverseColor(bgColor);

            Rectangle positionsBackground = new Rectangle(0, 278, 241, 1576);
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
        if (staff.getFanName() == null || staff.getFanName().isEmpty()) {
            Rectangle largeNameBg = new Rectangle(280, 1385, 850, 280);
            Color fgColor = Color.decode("#c3c2fe");
            b.drawStretchedCenteredString((staff.getFirstName() + " " + staff.getLastName()).trim(), largeNameBg, boldFont, fgColor, 1);
        } else {
            String[] names = buildNameList(staff.getFanName(), staff.getFirstName() + " " + staff.getLastName());
            Rectangle largeNameBg;
            if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isEmpty()) {
                largeNameBg = new Rectangle(280, 1385, 850, 280);
            } else {
                largeNameBg = new Rectangle(280, 1385, 850, 380);
            }
//        b.fillRect(largeNameBg, Color.ORANGE);
            Color fgColor = Color.decode("#c3c2fe");
            b.drawStretchedCenteredStrings(names, largeNameBg, boldFont, fgColor, 1);
        }
    }

    void drawPronouns(BadgeImage b, StaffBadgeDTO staff) {
        if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isBlank()) {
            Color fgColor = Color.decode("#c3c2fe");
            Rectangle background = new Rectangle(700, 1620, 460, 150);
//            b.fillRect(background, Color.RED);
            b.drawStretchedCenteredString(staff.getPreferredPronoun(), background, plainFont, fgColor, 1);
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

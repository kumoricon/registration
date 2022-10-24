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


    void drawPositionsStripe(BadgeImage b, StaffBadgeDTO staff) {
        // Guests don't have a department color stripe, but we have to fill in the background color this
        // year. (In past years, it was part of the badge background)
        Color fgColor = positionForeground(staff.getDepartmentBackgroundColor());
        Color bgColor = positionBackground(staff.getDepartmentBackgroundColor());
        if (staff.getDepartmentBackgroundColor() != null && !staff.getDepartmentBackgroundColor().isBlank()) {
            bgColor = Color.decode(staff.getDepartmentBackgroundColor());
            fgColor = BadgeImage.getInverseColor(bgColor);
        }

        Rectangle positionsBackground = new Rectangle(0, 278, 242, 1576);
        b.fillRect(positionsBackground, bgColor);

        if (staff.getPositions() != null) {
            Rectangle textBounds = new Rectangle(54, 290, 176, 1410);
//            b.fillRect(textBounds, Color.CYAN);
            b.drawRotatedCenteredStrings(staff.getPositions(), textBounds, boldFont, fgColor, true);
        }
    }

    static Color positionForeground(String departmentBackgroundColor) {
        if (departmentBackgroundColor == null || departmentBackgroundColor.isEmpty()) {
            return Color.BLACK;
        } else {
            return BadgeImage.getInverseColor(Color.decode(departmentBackgroundColor));
        }
    }

    static Color positionBackground(String departmentBackgroundColor) {
        if (departmentBackgroundColor == null || departmentBackgroundColor.isEmpty()) {
            return Color.decode("#c3c2fe");
        } else {
            return Color.decode(departmentBackgroundColor);
        }
    }



    void drawLargeName(BadgeImage b, StaffBadgeDTO staff) {
        // Staff don't have fan names, but guests do, so add that to the list if it's not null.
        // Also, some guests apparently don't have a last name entered.
        Color fgColor = foregroundColorForName(staff);
        String[] names = buildNameList(staff);
        Rectangle line1 = null;
        Rectangle line2 = null;

        if (staff.getPreferredPronoun() == null || staff.getPreferredPronoun().isBlank()) {
            // No pronoun
            if (names.length == 1) {
                line1 = new Rectangle(270, 1300, 880, 450);
                b.drawStretchedCenteredString(names[0], line1, boldFont, fgColor, 1);
            } else if (names.length == 2) {
                line1 = new Rectangle(270, 1300, 880, 290);
                line2 = new Rectangle(270, 1560, 880, 210);
                b.drawStretchedCenteredString(names[0], line1, boldFont, fgColor, 1);
                b.drawStretchedCenteredString(names[1], line2, boldFont, fgColor, 1);
            }
        } else {
            // Pronouns
            if (names.length == 1) {
                line1 = new Rectangle(270, 1300, 880, 350);
                b.drawStretchedCenteredString(names[0], line1, boldFont, fgColor, 1);
            } else if (names.length == 2) {
                line1 = new Rectangle(270, 1300, 880, 230);
                line2 = new Rectangle(270, 1500, 880, 160);
                b.drawStretchedCenteredString(names[0], line1, boldFont, fgColor, 1);
                b.drawStretchedCenteredString(names[1], line2, boldFont, fgColor, 1);
            }
        }
//        if (line1 != null) b.fillRect(line1, Color.RED);
//        if (line2 != null) b.fillRect(line2, Color.ORANGE);
    }

    private static Color foregroundColorForName(StaffBadgeDTO staff) {
        Color fgColor;
        if (staff.getDepartment() == null) {
            // No department == Guest of Honor
            fgColor = Color.decode("#39296c");
        } else {
            // Regular staff
            fgColor = Color.decode("#c3c2fe");
        }
        return fgColor;
    }

    void drawPronouns(BadgeImage b, StaffBadgeDTO staff) {
        if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isBlank()) {
            Color fgColor = foregroundColorForName(staff);
            Rectangle background = new Rectangle(700, 1620, 460, 150);
//            b.fillRect(background, Color.RED);
            b.drawStretchedCenteredString(staff.getPreferredPronoun(), background, plainFont, fgColor, 1);
        }
    }

    void drawBadgeNumber(BadgeImage b, StaffBadgeDTO staff) {
        Color fgColor = positionForeground(staff.getDepartmentBackgroundColor());
        if (staff.getDepartmentBackgroundColor() != null && !staff.getDepartmentBackgroundColor().isBlank()) {
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
     * Builds a two-line name to print based on the staff record. If fan name exists, use
     *     Fan Name
     *     First Last
     * otherwise, use:
     *     First
     *     Last
     * @param staff Staff record
     * @return Array
     */
    String[] buildNameList(StaffBadgeDTO staff) {
        java.util.List<String> names = new ArrayList<>();
        if (staff.getFanName() == null || staff.getFanName().isBlank()) {
            // No fan name set; first and last names on separate lines
            if (staff.getFirstName() != null && !staff.getFirstName().isBlank()) {
                names.add(staff.getFirstName());
            }
            if (staff.getLastName() != null && !staff.getLastName().isBlank()) {
                names.add(staff.getLastName());
            }
        } else {
            names.add(staff.getFanName());
            names.add((staff.getFirstName() + " " + staff.getLastName()).trim());
        }

        String[] output = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            output[i] = names.get(i);
        }
        return output;
    }
}

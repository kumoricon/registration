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

    // For 2023, an age image is not used (background color instead)
    // void drawBadgeAgeImage(BadgeImage b, StaffBadgeDTO staff) {
    //     Rectangle badgeImageLocation = new Rectangle(160, 292, 165, 113);
    //     b.drawImage(staff.getAgeImage(), badgeImageLocation);
    // }

    void drawPositionsStripe(BadgeImage b, StaffBadgeDTO staff) {
        // Guests don't have a department color stripe, but we have to fill in the background color this
        // year. (In past years, it was part of the badge background)
        Color fgColor = positionForeground(staff.getDepartmentBackgroundColor());
        Color bgColor = positionBackground(staff.getDepartmentBackgroundColor());

        Rectangle positionsBackground = new Rectangle(0, 625, 1200, 207);
        b.fillRect(positionsBackground, bgColor);

        String[] staffPositions = staff.getPositions();

        if (staffPositions != null) {
            Rectangle textBounds = null;

            if (staffPositions.length == 2) {
                textBounds = new Rectangle(80, 605, 1040, 207);
            } else {
                textBounds = new Rectangle(80, 605, 1040, 207);
            }

            float maxFontSize = 72f;
            boolean yAxisCentering = true;
            b.drawCenteredStrings(staffPositions, textBounds, boldFont, fgColor, 1, maxFontSize, yAxisCentering);
        }
    }

    static Color positionForeground(String departmentBackgroundColor) {
        if (departmentBackgroundColor == null || departmentBackgroundColor.isEmpty()) {
            return Color.WHITE;
        } else {
            return Color.WHITE;
        }
    }

    static Color positionBackground(String departmentBackgroundColor) {
        if (departmentBackgroundColor == null || departmentBackgroundColor.isEmpty()) {
            return Color.decode("#E69826");
        } else {
            return Color.decode(departmentBackgroundColor);
        }
    }

    void drawLargeName(BadgeImage b, StaffBadgeDTO staff) {
        // Staff don't have fan names, but guests do, so add that to the list if it's not null.
        // Also, some guests apparently don't have a last name entered.

        // Draw the background with the age color
        Color bgColor = Color.decode(staff.getAgeBackgroundColor());
        Rectangle positionsBackground = new Rectangle(0, 1513, 1200, 390);
        b.fillRect(positionsBackground, bgColor);

        Color fgColor = foregroundColorForName(staff);
        String[] names = buildNameList(staff);
        Rectangle line1 = null;

        if (names.length == 1) {
            line1 = new Rectangle(80, 1495, 1020, 200);
            float maxFontSize = 0.0f;             
            b.drawStretchedCenteredString(names[0], line1, boldFont, fgColor, 1, maxFontSize);
        } else if (names.length == 2) {
            line1 = new Rectangle(80, 1495, 1020, 200);
            float maxFontSize = 0.0f;
            b.drawStretchedCenteredString(names[0] + ' ' + names[1], line1, boldFont, fgColor, 1, maxFontSize);
        }
    }

    private static Color foregroundColorForName(StaffBadgeDTO staff) {
        Color fgColor;
        if (staff.getDepartment() == null) {
            // No department == Guest of Honor
            fgColor = Color.WHITE;
        } else {
            // Regular staff
            fgColor = Color.WHITE;
        }
        return fgColor;
    }

    void drawPronouns(BadgeImage b, StaffBadgeDTO staff) {
        if (staff.getPreferredPronoun() != null && !staff.getPreferredPronoun().isBlank()) {
            Color fgColor = Color.WHITE;
            Rectangle background = new Rectangle(810, 1675, 300, 70);
            Font pronounFont = plainFont.deriveFont(56f);
            b.drawRightAlignedString(staff.getPreferredPronoun(), background, pronounFont, fgColor, 0);
        }
    }

    void drawBadgeNumber(BadgeImage b, StaffBadgeDTO staff) {
        Color bgColor = null;
        if (staff.getDepartment() == null) {
            // No department == Guest of Honor
            bgColor = Color.BLACK;
        } else {
            // Regular staff
            bgColor = Color.decode(staff.getAgeBackgroundColor());
        }

        Color fgColor = Color.WHITE; //BadgeImage.getInverseColor(bgColor);
        if (staff.getBadgeNumber() != null && !staff.getBadgeNumber().isBlank()) {
            Rectangle background = new Rectangle(70, 1685, 200, 60);
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

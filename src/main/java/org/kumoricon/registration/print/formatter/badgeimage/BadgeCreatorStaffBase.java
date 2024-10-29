package org.kumoricon.registration.print.formatter.badgeimage;
import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import java.awt.*;
import java.util.ArrayList;

class BadgeCreatorStaffBase {
    static final int DPI = 300;
    static final int BADGE_WIDTH = (int) 4.25*DPI;
    static final int BADGE_HEIGHT = (int) 6.25*DPI;
    static final Color STAFF_TEXT_OUTLINE_COLOR = Color.decode("#47154B");
    static final Color GUEST_TEXT_OUTLINE_COLOR = Color.decode("#0E477F");

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

        Rectangle positionsBackground = new Rectangle(0, 615, 1200, 220);
        b.fillRect(positionsBackground, bgColor);

        String[] staffPositions = staff.getPositions();

        if (staffPositions != null) {
            Rectangle textBounds = null;
            // TODO: remove the y/height assignment after con, we're doing some major edge case stuff here boys
            // there's 2 staff members who have over 3 positions, both work with variable positioning
            final int y = staff.getPositions().length > 3 ? 585 : 605;
            final int height = staff.getPositions().length > 4 ? 200 : 220;
            textBounds = new Rectangle(80, y, 1040, height);

            float maxFontSize = 72f;
            boolean yAxisCentering = true;
            b.drawCenteredStrings(staffPositions, textBounds, boldFont, fgColor, getTextOutlineColor(staff), 1, maxFontSize, yAxisCentering);
        }
    }

    /**
     * hack way of doing this, but Guest & Staff badges have different text outlines
     * Guest DTOs have a null department background color, so checking that value to determine the outline we wanna use
     */
    Color getTextOutlineColor(final StaffBadgeDTO staff) {
        return staff.getDepartmentBackgroundColor().isEmpty() ? GUEST_TEXT_OUTLINE_COLOR : STAFF_TEXT_OUTLINE_COLOR;
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
        Rectangle positionsBackground = new Rectangle(8, 1513, 1200, 390);
        b.fillRect(positionsBackground, bgColor);

        Color fgColor = foregroundColorForName(staff);
        String[] names = buildNameList(staff);
        Rectangle line1 = null;

        line1 = new Rectangle(88, 1495, 1040, 200);
        b.drawStretchedCenteredString(
            names.length == 2 ? names[0] + ' ' + names[1] : names[0],
            line1,
            boldFont,
            fgColor,
            getTextOutlineColor(staff),
            1,
            0.0f
        );
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
            b.drawRightAlignedString(staff.getPreferredPronoun(), background, pronounFont, fgColor, getTextOutlineColor(staff), 1);
        }
    }

    void drawBadgeNumber(BadgeImage b, StaffBadgeDTO staff) {
        Color fgColor = Color.WHITE;
        if (staff.getBadgeNumber() != null && !staff.getBadgeNumber().isBlank()) {
            Rectangle background = new Rectangle(70, 1685, 200, 60);
            b.drawStretchedCenteredString(staff.getBadgeNumber(), background, plainFont, fgColor, getTextOutlineColor(staff), 1);
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

package org.kumoricon.registration.print.formatter.badgeimage;


import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BadgeCreatorStaff2018Front implements BadgeCreatorStaff {
    private static final int DPI = 300;
    private static final int BADGE_WIDTH = (int) 4.25*DPI;      // 4x6 inch badge with overprint
    private static final int BADGE_HEIGHT = (int) 6.25*DPI;
    private Font font;

    @Override
    public byte[] createBadge(StaffBadgeDTO attendee) {
        if (font == null) {
            font = new Font("Dialog", Font.BOLD, 36);
        }
        BadgeImage b = new BadgeImage(BADGE_WIDTH, BADGE_HEIGHT, DPI);

        drawBadgeTypeStripe(b, attendee);
        drawAgeColorStripe(b, attendee);
        drawName(b, attendee);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(b.getImage(), "png", baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public byte[] createBadge(StaffBadgeDTO attendee, Font font) {
        this.font = font;
        return createBadge(attendee);
    }

    @SuppressWarnings("Duplicates")
    private void drawAgeColorStripe(BadgeImage b, StaffBadgeDTO attendee) {
        Rectangle ageBackground = new Rectangle(0, 1560, BADGE_WIDTH, 225);
        Color bgColor = Color.decode(attendee.getAgeBackgroundColor());
        Color fgColor = BadgeImage.getInverseColor(bgColor);
        b.fillRect(ageBackground, bgColor);

        b.drawStretchedCenteredString("STAFF", ageBackground, font, fgColor);
    }

    private void drawName(BadgeImage b, StaffBadgeDTO attendee) {
        Rectangle nameBg = new Rectangle(235, 1250, 650, 300);
        String[] names = {attendee.getFirstName(), attendee.getLastName()};
        b.drawCenteredStrings(names, nameBg, font, Color.BLACK);
    }

    private void drawBadgeTypeStripe(BadgeImage b, StaffBadgeDTO attendee) {
        if (attendee != null) {
            Color bgColor = Color.decode(attendee.getDepartmentBackgroundColor());
            Color fgColor = BadgeImage.getInverseColor(bgColor);
            Rectangle positionsBackground = new Rectangle(0, 0, 218, 1575);
            b.fillRect(positionsBackground, bgColor);

            Rectangle textBounds = new Rectangle(45, 50, 150, 1475);
            b.drawRotatedCenteredStrings(attendee.getPositions(), textBounds, font, fgColor, true);
        }
    }
}

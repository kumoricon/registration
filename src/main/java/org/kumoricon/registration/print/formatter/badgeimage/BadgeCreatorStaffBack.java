package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import java.awt.*;

public class BadgeCreatorStaffBack extends BadgeCreatorStaffBase {
    public BadgeCreatorStaffBack(Font badgeFont, Font nameFont) {
        super(badgeFont, nameFont);
    }

    public byte[] createBadge(StaffBadgeDTO staff) {
        BadgeImage b = new BadgeImage(BADGE_WIDTH, BADGE_HEIGHT, DPI);

        drawBadgeTypeStripe(b, staff);
        drawLargeName(b, staff);
        drawBadgeImage(b, staff);
        drawPronouns(b, staff);
        drawBadgeAgeImage(b, staff);

        return b.writePNGToByteArray();
    }

    @Override
    void drawBadgeAgeImage(BadgeImage b, StaffBadgeDTO staff) {
        Rectangle badgeImageLocation = new Rectangle(216, 290, 158, 410);
        b.drawStretchedImage(staff.getAgeImage(), badgeImageLocation);
    }


    private void drawBadgeImage(BadgeImage b, StaffBadgeDTO staff) {
        Rectangle badgeImageLocation = new Rectangle(400, 370, 500, 500);
        b.drawStretchedImage(staff.getBadgeImage(), badgeImageLocation);
    }
}

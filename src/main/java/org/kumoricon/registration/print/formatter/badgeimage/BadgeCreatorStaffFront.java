package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;
import java.awt.*;

public class BadgeCreatorStaffFront extends BadgeCreatorStaffBase {
    public BadgeCreatorStaffFront(Font boldFont, Font plainFont) {
        super(boldFont, plainFont);
    }

    public byte[] createBadge(StaffBadgeDTO staff) {
        BadgeImage b = new BadgeImage(BADGE_WIDTH, BADGE_HEIGHT, DPI);

        drawLargeName(b, staff);
        drawPositionsStripe(b, staff);
        drawPronouns(b, staff);
        drawBadgeNumber(b, staff);
        drawBadgeImage(b, staff);

        return b.writePNGToByteArray();
    }

    private void drawBadgeImage(BadgeImage b, StaffBadgeDTO staff) {
        Rectangle badgeImageLocation = new Rectangle(80, 850, 1040, 510);
        b.drawStretchedImage(staff.getBadgeImage(), badgeImageLocation);
    }
}

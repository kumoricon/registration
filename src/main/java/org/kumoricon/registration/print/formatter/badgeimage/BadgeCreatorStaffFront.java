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
        drawBadgeAgeImage(b, staff);
        drawBadgeNumber(b, staff);
        return b.writePNGToByteArray();
    }
}

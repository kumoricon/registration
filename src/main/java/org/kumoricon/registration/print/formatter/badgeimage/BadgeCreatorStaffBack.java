package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;

import java.awt.*;

public class BadgeCreatorStaffBack extends BadgeCreatorStaffBase {
    public BadgeCreatorStaffBack(Font boldFont, Font plainFont) {
        super(boldFont, plainFont);
    }

    public byte[] createBadge(StaffBadgeDTO staff) {
        BadgeImage b = new BadgeImage(BADGE_WIDTH, BADGE_HEIGHT, DPI);

        drawPositionsStripe(b, staff);
        drawLargeName(b, staff);
        drawPronouns(b, staff);
        drawBadgeNumber(b, staff);

        return b.writePNGToByteArray();
    }
}

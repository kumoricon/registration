package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.staff.StaffBadgeDTO;

public interface BadgeCreatorStaff {
    /**
     * Creates a badge image from the given information and returns a byte array
     * of the PNG.
     * @param attendee Attendee data
     * @return PNG byte array
     */
    byte[] createBadge(StaffBadgeDTO attendee);
}

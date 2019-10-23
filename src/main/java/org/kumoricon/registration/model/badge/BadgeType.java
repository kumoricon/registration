package org.kumoricon.registration.model.badge;

/**
 * Represents the type of badge, used for calculations and reporting
 */
public enum BadgeType {
    ATTENDEE,       // Regular attendee badges - weekend, day badges
    VIP,
    SPECIALTY,      // Industry, press, artist, etc
    STAFF,
    GUEST;          // Guest of Honor

    public static BadgeType of (int badgeType) {
        if (badgeType == 0) {
            return ATTENDEE;
        } else if (badgeType == 1) {
            return VIP;
        } else  if (badgeType == 2) {
            return SPECIALTY;
        } else if (badgeType == 3) {
            return STAFF;
        } else if (badgeType == 4) {
            return GUEST;
        } else {
            return ATTENDEE;
        }
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

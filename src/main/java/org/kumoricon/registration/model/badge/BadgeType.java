package org.kumoricon.registration.model.badge;

/**
 * Represents the type of badge, used for calculations and reporting
 */
public enum BadgeType {
    ATTENDEE, STAFF, OTHER;

    public static BadgeType of (int badgeType) {
        if (badgeType == 0) {
            return ATTENDEE;
        } else if (badgeType == 1) {
            return STAFF;
        } else {
            return OTHER;
        }
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

package org.kumoricon.registration.model.badge;

public class BadgeFactory {
    /**
     * Create a Badge object with age ranges for adult/youth/child/5 and under.
     * @param name Fan Name
     * @param badgeTypeText Text to print in color stripe (Weekend/Friday/Exhibitor/etc)
     * @param dayBackgroundColor HTML color code for day background color ("#00FF00")
     * @param adultCost Cost of adult badge
     * @param youthCost Cost of youth badge
     * @param childCost Cost of child badge
     * @return Badge
     */
    public static Badge createBadge(String name, BadgeType badgeType, String badgeTypeText, String dayBackgroundColor,
                                    double adultCost, double youthCost, double childCost) {
        Badge b = new Badge();
        b.setName(name);
        b.setBadgeType(badgeType);
        b.setBadgeTypeText(badgeTypeText);
        b.setBadgeTypeBackgroundColor(dayBackgroundColor);
        b.addAgeRange("Adult", 18, 255, adultCost, "#412B78", "Adult");
        b.addAgeRange("Youth", 13, 17, youthCost, "#EDB741", "Youth");
        b.addAgeRange("Child", 6, 12, childCost, "#BF2D28", "Child");
        b.addAgeRange("5 and under", 0, 5, 0.00, "#BF2D28", "Child");
        return b;
    }

    /**
     * Creates a Badge object with default age ranges
     * @return Badge
     */
    public static Badge createEmptyBadge() {
        Badge b = new Badge();
        b.setName("");
        b.setBadgeType(BadgeType.ATTENDEE);
        b.setBadgeTypeText("");
        b.addAgeRange("Adult", 18, 255, 0.00, "#412B78", "Adult");
        b.addAgeRange("Youth", 13, 17, 0.00, "#EDB741", "Youth");
        b.addAgeRange("Child", 6, 12, 0.00, "#BF2D28", "Child");
        b.addAgeRange("5 and under", 0, 5, 0.00, "#BF2D28", "Child");
        return b;
    }
}

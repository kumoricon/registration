package org.kumoricon.registration.model.badge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Badge {
    private Integer id;
    private String name;
    private String badgeTypeText;     // Friday/Saturday/Sunday/Weekend/VIP
    private String badgeTypeBackgroundColor;   // Background color for the day text
    private String warningMessage;
    private boolean visible;
    private List<AgeRange> ageRanges;
    private String requiredRight;       // Only show to users who have this right, or all if null
    private BadgeType badgeType;

    public Badge() {
        visible = true;
        ageRanges = new ArrayList<>(4);
    }

    public Badge(String name) {
        this();
        setName(name);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBadgeTypeText() { return badgeTypeText; }
    public void setBadgeTypeText(String day) { this.badgeTypeText = day; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public String getRequiredRight() { return requiredRight; }
    public void setRequiredRight(String requiredRight) { this.requiredRight = requiredRight; }

    public String getWarningMessage() { return warningMessage; }
    public void setWarningMessage(String warningMessage) { this.warningMessage = warningMessage; }

    public BadgeType getBadgeType() { return badgeType; }

    public void setBadgeType(BadgeType badgeType) { this.badgeType = badgeType; }

    public List<AgeRange> getAgeRanges() { return ageRanges; }
    public void addAgeRange(String name, int minAge, int maxAge, double cost, String stripeColor, String stripeText) {
        AgeRange a = new AgeRange(name, minAge, maxAge, cost, stripeColor, stripeText, this.id);
        ageRanges.add(a);
    }

    public String getBadgeTypeBackgroundColor() {
        return badgeTypeBackgroundColor;
    }

    public void setBadgeTypeBackgroundColor(String badgeTypeBackgroundColor) {
        this.badgeTypeBackgroundColor = badgeTypeBackgroundColor;
    }

    public BigDecimal getCostForAge(Long age) {
        // No birthdate? Charge for adult badge
        if (age == null) { return getCostForAge(40L); }
        for (AgeRange ageRange : ageRanges) {
            if (ageRange.isValidForAge(age)) {
                return ageRange.getCost();
            }
        }
        throw new RuntimeException("No matching age range found for age " + age + " in " + name);
    }

    public AgeRange getAgeRangeForAge(Long age) {
        if (age != null) {
            for (AgeRange ageRange : ageRanges) {
                if (ageRange.isValidForAge(age)) {
                    return ageRange;
                }
            }
        } else {
            return getAgeRangeForAge(1L);   // If no age, return age range for 7. IE, assume they're a minor
        }
        return null;
    }


    public String toString() {
        if (id != null) {
            return String.format("[Badge %s: %s]", id, name);
        } else {
            return String.format("[Badge: %s]", name);
        }
    }

    public void setAgeRanges(List<AgeRange> ageRanges) {
        this.ageRanges = ageRanges;
    }
}

package org.kumoricon.registration.model.badge;

import java.math.BigDecimal;

public class AgeRange {
    private Integer id;
    private String name;
    private int minAge;
    private int maxAge;
    private String stripeColor;
    private String stripeText;
    private BigDecimal cost;
    private Integer badgeId;

    public AgeRange(String name, int minAge, int maxAge, BigDecimal cost, String stripeColor, String stripeText, Integer badgeId) {
        this.name = name;
        setMinAge(minAge);
        setMaxAge(maxAge);
        setCost(cost);
        this.stripeColor = stripeColor;
        this.stripeText = stripeText;
        this.badgeId = badgeId;
    }

    public AgeRange() { this("", 0, 255, BigDecimal.ZERO, "", "", null); }
    public AgeRange(String name, int minAge, int maxAge, double cost, String stripeColor, String stripeText, Integer badgeId) {
        this(name, minAge, maxAge, new BigDecimal(cost), stripeColor, stripeText, badgeId);
    }

    public String getStripeColor() { return stripeColor; }
    public void setStripeColor(String stripeColor) { this.stripeColor = stripeColor; }

    public String getStripeText() { return stripeText; }
    public void setStripeText(String stripeText) { this.stripeText = stripeText; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMinAge() { return minAge; }
    public void setMinAge(int minAge) {
        if (minAge < 0) {
            this.minAge = 0;
        } else this.minAge = Math.min(minAge, 255);
    }

    public int getMaxAge() { return maxAge; }
    public void setMaxAge(int maxAge) {
        if (maxAge < 0) {
            this.maxAge = 0;
        } else this.maxAge = Math.min(maxAge, 255);
    }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) {
        if (cost.compareTo(BigDecimal.ZERO) >= 0) {
            this.cost = cost;
        } else {
            this.cost = BigDecimal.ZERO;
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getBadgeId() { return badgeId; }
    public void setBadgeId(Integer badgeId) { this.badgeId = badgeId; }

    public boolean isValidForAge(long age) {
        return (age >= minAge && age <= maxAge);
    }

    public boolean isValidForAge(Integer age) {
        return age != null && (age >= minAge && age <= maxAge);
    }

    public String toString() {
        return String.format("[AgeRange: %s (%s-%s): $%s]", name, minAge, maxAge, cost.setScale(2).toString());
    }
}

package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.badge.AgeRange;
import org.kumoricon.registration.model.badge.Badge;

@SuppressWarnings("unused")
public class AttendeeBadgeDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String fanName;
    private String ageStripeBackgroundColor;
    private String ageStripeText;
    private String badgeTypeBackgroundColor;
    private String badgeTypeText;
    private String badgeNumber;

    public AttendeeBadgeDTO() {
        this.ageStripeBackgroundColor = "#323E99";
        this.ageStripeText = "Adult";
        this.badgeTypeBackgroundColor = "#626262";
        this.badgeTypeText= "Weekend";
        this.badgeNumber = "ONL12345";
    }

    public AttendeeBadgeDTO(String firstName, String lastName, String fanName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.fanName = fanName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFanName() {
        return fanName;
    }

    public void setFanName(String fanName) {
        this.fanName = fanName;
    }

    public String getAgeStripeBackgroundColor() {
        return ageStripeBackgroundColor;
    }

    public void setAgeStripeBackgroundColor(String ageStripeBackgroundColor) {
        this.ageStripeBackgroundColor = ageStripeBackgroundColor;
    }

    public String getAgeStripeText() {
        return ageStripeText;
    }

    public void setAgeStripeText(String ageStripeText) {
        this.ageStripeText = ageStripeText;
    }

    public String getBadgeTypeBackgroundColor() {
        return badgeTypeBackgroundColor;
    }

    public void setBadgeTypeBackgroundColor(String badgeTypeBackgroundColor) {
        this.badgeTypeBackgroundColor = badgeTypeBackgroundColor;
    }

    public String getBadgeTypeText() {
        return badgeTypeText;
    }

    public void setBadgeTypeText(String badgeTypeText) {
        this.badgeTypeText = badgeTypeText;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public static AttendeeBadgeDTO fromAttendee(Attendee attendee, Badge badge) {
        AttendeeBadgeDTO output = new AttendeeBadgeDTO();
        output.setId(attendee.getId());
        output.setFirstName(attendee.getFirstName());
        output.setLastName(attendee.getLastName());
        output.setFanName(attendee.getFanName());

        output.setBadgeNumber(attendee.getBadgeNumber());

        output.setBadgeTypeText(badge.getBadgeTypeText());
        output.setBadgeTypeBackgroundColor(badge.getBadgeTypeBackgroundColor());

        AgeRange ageRange = badge.getAgeRangeForAge(attendee.getAge());
        output.setAgeStripeText(ageRange.getStripeText());
        output.setAgeStripeBackgroundColor(ageRange.getStripeColor());

        return output;
    }

}

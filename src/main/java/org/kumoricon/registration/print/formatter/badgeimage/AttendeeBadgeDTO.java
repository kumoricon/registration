package org.kumoricon.registration.print.formatter.badgeimage;

import org.kumoricon.registration.guest.Guest;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.badge.AgeRange;
import org.kumoricon.registration.model.badge.Badge;

@SuppressWarnings("unused")
public class AttendeeBadgeDTO {
    private Integer id;
    private String name;
    private String fanName;
    private String ageStripeBackgroundColor;
    private String ageStripeText;
    private String badgeTypeBackgroundColor;
    private String badgeTypeText;
    private String badgeNumber;
    private String pronoun;

    public AttendeeBadgeDTO() {
        this.ageStripeBackgroundColor = "#323E99";
        this.ageStripeText = "Adult";
        this.badgeTypeBackgroundColor = "#626262";
        this.badgeTypeText= "Weekend";
        this.badgeNumber = "ONL12345";
    }

    public AttendeeBadgeDTO(String firstName, String lastName, String fanName) {
        setName(firstName, lastName);
        this.fanName = nullHandler(fanName);
    }

    private String nullHandler(String input) {
        if(input == null)
            return "";

        return input;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public void setName(String name) { this.name = nullHandler(name); }

    public void setName(String firstName, String lastName) {
        String nameBuilder = nullHandler(firstName).trim() +
                " " +
                nullHandler(lastName).trim();
        this.name = nameBuilder.trim();
    }

    public String getName() {
        return name;
    }

    public String getFanName() {
        return fanName;
    }

    public void setFanName(String fanName) { this.fanName = nullHandler(fanName); }

    public String getAgeStripeBackgroundColor() {
        return ageStripeBackgroundColor;
    }

    public void setAgeStripeBackgroundColor(String ageStripeBackgroundColor) {
        this.ageStripeBackgroundColor = nullHandler( ageStripeBackgroundColor );
    }

    public String getAgeStripeText() {
        return ageStripeText;
    }

    public void setAgeStripeText(String ageStripeText) {
        this.ageStripeText = nullHandler(ageStripeText);
    }

    public String getBadgeTypeBackgroundColor() {
        return badgeTypeBackgroundColor;
    }

    public void setBadgeTypeBackgroundColor(String badgeTypeBackgroundColor) {
        this.badgeTypeBackgroundColor = nullHandler(badgeTypeBackgroundColor);
    }

    public String getBadgeTypeText() {
        return badgeTypeText;
    }

    public void setBadgeTypeText(String badgeTypeText) {
        this.badgeTypeText = nullHandler(badgeTypeText);
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = nullHandler(badgeNumber);
    }

    public String getPronoun() { return pronoun; }
    public void setPronoun(String pronoun) { this.pronoun = nullHandler(pronoun); }

    public static AttendeeBadgeDTO fromAttendee(Attendee attendee, Badge badge) {
        AttendeeBadgeDTO output = new AttendeeBadgeDTO();
        output.setId(attendee.getId());
        output.setName(attendee.getFirstName(), attendee.getLastName());
        output.setFanName(attendee.getFanName());
        output.setBadgeNumber(attendee.getBadgeNumber());

        String attendeePronoun = determineAttendeePronoun(attendee);
        output.setPronoun(attendeePronoun);

        output.setBadgeTypeText(badge.getBadgeTypeText());
        output.setBadgeTypeBackgroundColor(badge.getBadgeTypeBackgroundColor());

        AgeRange ageRange = badge.getAgeRangeForAge(attendee.getAge());
        output.setAgeStripeText(ageRange.getStripeText());
        output.setAgeStripeBackgroundColor(ageRange.getStripeColor());

        return output;
    }

    public static AttendeeBadgeDTO fromGuest(Guest guest) {
        AttendeeBadgeDTO output = new AttendeeBadgeDTO();
        output.setId(guest.getId());
        output.setName(guest.getName());
        output.setFanName(guest.getFanName());
        output.setBadgeNumber("");
        output.setBadgeTypeText("Guest of Honor");
        output.setBadgeTypeBackgroundColor("#FF0099");
        output.setAgeStripeBackgroundColor("#FF0099");
        output.setAgeStripeText("Guest");
        return output;
    }

    private static String determineAttendeePronoun(Attendee attendee) {
        if (attendee.getCustomPronoun() == null || attendee.getPreferredPronoun().isEmpty()) {
            return attendee.getPreferredPronoun();
        }

        return attendee.getCustomPronoun();
    }
}

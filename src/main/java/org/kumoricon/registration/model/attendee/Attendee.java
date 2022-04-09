package org.kumoricon.registration.model.attendee;


import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.order.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class Attendee {
    private Integer id;
    private String firstName;
    private String lastName;
    private String legalFirstName;
    private String legalLastName;
    private Boolean nameIsLegalName;
    private String preferredPronoun;
    private String customPronoun;
    private String fanName;                   // Fan Name (optional)
    private String badgeNumber;
    private String zip;
    private String country;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private String emergencyContactFullName;
    private String emergencyContactPhone;
    private Boolean parentIsEmergencyContact;   // is emergency contact same as parent?
    private String parentFullName;
    private String parentPhone;
    private Boolean parentFormReceived;         // has parental consent form been received?
    private Boolean paid;                       // has attendee paid? True for $0 attendees (press/comped/etc)
    private BigDecimal paidAmount;              // Amount paid - not necessarily the same as the badge cost, but
                                                // usually should be
    private Boolean compedBadge;                // True if the badge has been comped -- IE, is free
    private Integer badgeId;
    private Integer orderId;
    private Boolean checkedIn;                  // Has attendee checked in and received badge?
    private OffsetDateTime checkInTime;         // Timestamp when checked in
    private boolean preRegistered;              // Did attendee register before con?
    private boolean badgePrePrinted;            // Is a preprinted badge ready for this attendee?
    private boolean badgePrinted;               // Has badge been printed before
    private boolean membershipRevoked;          // If false, attendee may be checked in and badge may be reprinted
    private boolean accessibilitySticker;
    private OffsetDateTime lastModified;
    // Possible values for Preferred pronoun field
    public static final List<String> PRONOUNS = Arrays.asList(null, "He/Him", "She/Her", "They/Them", "He/They", "She/They", "Ask Me My Pronouns");

    public Attendee() {
        this.paidAmount = null;
        this.checkedIn = false;
        this.paid = false;
        this.preRegistered = false;
        this.compedBadge = false;
        this.parentIsEmergencyContact = false;
        this.badgePrePrinted = false;
        this.badgePrinted = false;
        this.nameIsLegalName = true;
        this.membershipRevoked = false;
        this.accessibilitySticker = false;
    }


    /**
     * Returns true if attendee is < 18 years old or birthdate isn't set
     * @return minor status
     */
    public boolean isMinor() {
        // If birthdate isn't set for some reason, treat them as a minor.
        LocalDate now = LocalDate.now(ZoneId.of("America/Los_Angeles"));
        return birthDate == null || birthDate.isAfter(now.minusYears(18));
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getName() {
        return (firstName + " " + lastName).trim();
    }

    public String getPreferredPronoun() { return preferredPronoun; }
    public void setPreferredPronoun(String preferredPronoun) { this.preferredPronoun = preferredPronoun; }

    public String getCustomPronoun() { return customPronoun; }
    public void setCustomPronoun(String customPronoun) { this.customPronoun = customPronoun; }

    public String getFanName() { return fanName; }
    public void setFanName(String fanName) { this.fanName = fanName; }

    public String getBadgeNumber() { return badgeNumber; }
    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getNameIsLegalName() {
        return nameIsLegalName;
    }

    public void setNameIsLegalName(Boolean nameIsLegalName) {
        this.nameIsLegalName = nameIsLegalName;
    }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public Long getAge() {
        if (birthDate == null) { return 0L; }
        LocalDate now = LocalDate.now(ZoneId.of("America/Los_Angeles"));
        return ChronoUnit.YEARS.between(birthDate, now);
    }

    public Long getAge(LocalDate date) {
        if (birthDate == null) { return 0L; }
        return ChronoUnit.YEARS.between(birthDate, date);
    }

    public String getEmergencyContactFullName() { return emergencyContactFullName; }
    public void setEmergencyContactFullName(String emergencyContactFullName) {
        this.emergencyContactFullName = emergencyContactFullName;
    }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public Boolean getParentIsEmergencyContact() { return parentIsEmergencyContact; }
    public void setParentIsEmergencyContact(Boolean parentIsEmergencyContact) { this.parentIsEmergencyContact = parentIsEmergencyContact; }

    public String getParentFullName() { return parentFullName; }
    public void setParentFullName(String parentFullName) { this.parentFullName = parentFullName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public Boolean getParentFormReceived() { return parentFormReceived; }
    public void setParentFormReceived(Boolean parentFormReceived) { this.parentFormReceived = parentFormReceived; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public Boolean getCompedBadge() { return compedBadge; }
    public void setCompedBadge(Boolean compedBadge) { this.compedBadge = compedBadge; }

    public Integer getBadgeId() { return badgeId; }
    public void setBadgeId(Integer badgeId) { this.badgeId = badgeId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Badge getBadge() { return null; }
    public void setBadge(Badge badge) {
        this.badgeId = badge.getId();
    }

    public void setOrder(Order order) { this.orderId = order.getId(); }

    public String getLegalFirstName() { return legalFirstName; }
    public void setLegalFirstName(String legalFirstName) { this.legalFirstName = legalFirstName; }

    public String getLegalLastName() { return legalLastName; }
    public void setLegalLastName(String legalLastName) { this.legalLastName = legalLastName; }

    public boolean isBadgePrePrinted() {
        return badgePrePrinted;
    }

    public void setBadgePrePrinted(boolean badgePrePrinted) {
        this.badgePrePrinted = badgePrePrinted;
    }

    public boolean isBadgePrinted() {
        return badgePrinted;
    }

    public void setBadgePrinted(boolean badgePrinted) {
        this.badgePrinted = badgePrinted;
    }

    public Boolean getCheckedIn() { return checkedIn; }
    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
        if (checkedIn) {
            if (checkInTime == null) {
                checkInTime = OffsetDateTime.now();
            }
        } else {
            checkInTime = null;
        }
    }

    public void setCheckInTime(OffsetDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public OffsetDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setPreRegistered(boolean preRegistered) { this.preRegistered = preRegistered; }
    public Boolean isPreRegistered() { return preRegistered; }
    public Boolean getPreRegistered() { return preRegistered; }

    public void setMembershipRevoked(boolean membershipRevoked) { this.membershipRevoked = membershipRevoked; }
    public boolean isMembershipRevoked() { return membershipRevoked; }

    public boolean getAccessibilitySticker() { return accessibilitySticker; }
    public boolean hasAccessibilitySticker() { return accessibilitySticker; }
    public void setAccessibilitySticker(boolean accessibilitySticker) { this.accessibilitySticker = accessibilitySticker; }

    public OffsetDateTime getLastModified() { return lastModified; }
    public void setLastModified(OffsetDateTime lastModified) { this.lastModified = lastModified;}

    @Override
    public String toString() {
        if (id != null) {
            return String.format("[Attendee %s: %s]", id, getNameOrFanName());
        } else {
            return String.format("[Attendee: %s]", getNameOrFanName());
        }
    }

    public String getNameOrFanName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty())
            sb.append(firstName);
            sb.append(" ");
        if (lastName != null) sb.append(lastName);

        if (sb.toString().trim().length() > 0) {
            return sb.toString().trim();
        } else {
            return fanName;
        }
    }

    public void updateFrom(Attendee other) {
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
        this.legalFirstName = other.getLegalFirstName();
        this.legalLastName = other.getLegalLastName();
        this.nameIsLegalName = other.getNameIsLegalName();
        this.fanName = other.getFanName();
        this.customPronoun = other.getCustomPronoun();
        this.preferredPronoun = other.getPreferredPronoun();
        this.birthDate = other.getBirthDate();
        this.phoneNumber = other.getPhoneNumber();
        this.email = other.getEmail();
        this.zip = other.getZip();
        this.country = other.getCountry();
        this.emergencyContactFullName = other.getEmergencyContactFullName();
        this.emergencyContactPhone = other.getEmergencyContactPhone();
        this.parentFullName = other.getParentFullName();
        this.parentPhone = other.getParentPhone();
        this.badgeId = other.getBadgeId();
        this.parentFormReceived = other.getParentFormReceived();
    }
}

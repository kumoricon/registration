package org.kumoricon.registration.model.attendee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class AttendeeDetailDTO {
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
    private Long age;
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
    private String badgeType;
    private Integer orderId;
    private Boolean checkedIn;                  // Has attendee checked in and received badge?
    private OffsetDateTime checkInTime;         // Timestamp when checked in
    private boolean preRegistered;              // Did attendee register before con?
    private boolean badgePrePrinted;            // Is a preprinted badge ready for this attendee?
    private boolean badgePrinted;               // Has badge been printed before
    private boolean membershipRevoked;
    private boolean accessibilitySticker;
    private OffsetDateTime lastModified;

    AttendeeDetailDTO() {}


    public boolean isMinor() {
        // If birthdate isn't set, treat them as a minor.
        if (age == null) return true;
        return age < 18;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getName() {
        return (firstName + " " + lastName).trim();
    }

    public String getLegalFirstName() {
        return legalFirstName;
    }

    public void setLegalFirstName(String legalFirstName) {
        this.legalFirstName = legalFirstName;
    }

    public String getLegalLastName() {
        return legalLastName;
    }

    public void setLegalLastName(String legalLastName) {
        this.legalLastName = legalLastName;
    }

    public Boolean getNameIsLegalName() {
        return nameIsLegalName;
    }

    public void setNameIsLegalName(Boolean nameIsLegalName) {
        this.nameIsLegalName = nameIsLegalName;
    }

    public String getPreferredPronoun() {
        return preferredPronoun;
    }

    public void setPreferredPronoun(String preferredPronoun) {
        this.preferredPronoun = preferredPronoun;
    }

    public String getCustomPronoun() {
        return customPronoun;
    }

    public void setCustomPronoun(String customPronoun) {
        this.customPronoun = customPronoun;
    }

    public String getFanName() {
        return fanName;
    }

    public void setFanName(String fanName) {
        this.fanName = fanName;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getEmergencyContactFullName() {
        return emergencyContactFullName;
    }

    public void setEmergencyContactFullName(String emergencyContactFullName) {
        this.emergencyContactFullName = emergencyContactFullName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public Boolean getParentIsEmergencyContact() {
        return parentIsEmergencyContact;
    }

    public void setParentIsEmergencyContact(Boolean parentIsEmergencyContact) {
        this.parentIsEmergencyContact = parentIsEmergencyContact;
    }

    public String getParentFullName() {
        return parentFullName;
    }

    public void setParentFullName(String parentFullName) {
        this.parentFullName = parentFullName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public Boolean getParentFormReceived() {
        return parentFormReceived;
    }

    public void setParentFormReceived(Boolean parentFormReceived) {
        this.parentFormReceived = parentFormReceived;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Boolean getCompedBadge() {
        return compedBadge;
    }

    public void setCompedBadge(Boolean compedBadge) {
        this.compedBadge = compedBadge;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public OffsetDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(OffsetDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public boolean isPreRegistered() {
        return preRegistered;
    }

    public void setPreRegistered(boolean preRegistered) {
        this.preRegistered = preRegistered;
    }

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

    public boolean isMembershipRevoked() { return membershipRevoked; }

    public void setMembershipRevoked(boolean membershipRevoked) { this.membershipRevoked = membershipRevoked; }

    public boolean getAccessibilitySticker() { return accessibilitySticker; }

    public void setAccessibilitySticker(boolean accessibilitySticker) { this.accessibilitySticker = accessibilitySticker; }

    public OffsetDateTime getLastModified() { return lastModified; }

    public void setLastModified(OffsetDateTime lastModified) { this.lastModified = lastModified; }

    @Override
    public String toString() {
        if (id != null) {
            return String.format("[Attendee %s: %s]", id, getNameOrFanName());
        } else {
            return String.format("[Attendee: %s]", getNameOrFanName());
        }
    }

    @SuppressWarnings("Duplicates")
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
}

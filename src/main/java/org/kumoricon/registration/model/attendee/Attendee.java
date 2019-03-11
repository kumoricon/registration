package org.kumoricon.registration.model.attendee;


import org.kumoricon.registration.model.Record;
import org.kumoricon.registration.model.badge.AgeRange;
import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.order.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class Attendee extends Record {
    private String firstName;
    private String lastName;
    private String legalFirstName;
    private String legalLastName;
    private Boolean nameIsLegalName;
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
    private Instant checkInTime;                    // Timestamp when checked in
    private boolean preRegistered;              // Did attendee register before con?
    private boolean badgePrePrinted;            // Is a preprinted badge ready for this attendee?
    private boolean badgePrinted;               // Has badge been printed before


    public Attendee() {
        this.paidAmount = BigDecimal.ZERO;
        this.checkedIn = false;
        this.paid = false;
        this.preRegistered = false;
        this.compedBadge = false;
        this.parentIsEmergencyContact = false;
        this.badgePrePrinted = false;
        this.badgePrinted = false;
        this.nameIsLegalName = true;
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

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getName() { return firstName + " " + lastName; }

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
                checkInTime = Instant.now();
            }
        } else {
            checkInTime = null;
        }
    }

    public void setCheckInTime(Instant checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Instant getCheckInTime() {
        return checkInTime;
    }

    public void setPreRegistered(boolean preRegistered) { this.preRegistered = preRegistered; }
    public Boolean isPreRegistered() { return preRegistered; }

    @Override
    public String toString() {
        if (id != null) {
            return String.format("[Attendee %s: %s %s]", id, firstName, lastName);
        } else {
            return String.format("[Attendee: %s %s]", firstName, lastName);
        }
    }

    public AgeRange getCurrentAgeRange() {
//        if (birthDate != null && badge != null) {
//            for (AgeRange ageRange : getBadge().getAgeRanges()) {
//                if (ageRange.isValidForAge(getAge())) {
//                    return ageRange;
//                }
//            }
//        }
        return null;
    }


//    public boolean fieldsSameAs(Attendee attendee) {
//        if (attendee == null) return false;
//
//        if (preRegistered != attendee.preRegistered) return false;
//        if (badgePrePrinted != attendee.badgePrePrinted) return false;
//        if (badgePrinted != attendee.badgePrinted) return false;
//        if (firstName != null ? !firstName.equals(attendee.firstName) : attendee.firstName != null) return false;
//        if (lastName != null ? !lastName.equals(attendee.lastName) : attendee.lastName != null) return false;
//        if (legalFirstName != null ? !legalFirstName.equals(attendee.legalFirstName) : attendee.legalFirstName != null)
//            return false;
//        if (legalLastName != null ? !legalLastName.equals(attendee.legalLastName) : attendee.legalLastName != null)
//            return false;
//        if (nameIsLegalName != null ? !nameIsLegalName.equals(attendee.nameIsLegalName) : attendee.nameIsLegalName != null)
//            return false;
//        if (fanName != null ? !fanName.equals(attendee.fanName) : attendee.fanName != null) return false;
//        if (badgeNumber != null ? !badgeNumber.equals(attendee.badgeNumber) : attendee.badgeNumber != null)
//            return false;
//        if (zip != null ? !zip.equals(attendee.zip) : attendee.zip != null) return false;
//        if (country != null ? !country.equals(attendee.country) : attendee.country != null) return false;
//        if (phoneNumber != null ? !phoneNumber.equals(attendee.phoneNumber) : attendee.phoneNumber != null)
//            return false;
//        if (email != null ? !email.equals(attendee.email) : attendee.email != null) return false;
//        if (birthDate != null ? !birthDate.equals(attendee.birthDate) : attendee.birthDate != null) return false;
//        if (emergencyContactFullName != null ? !emergencyContactFullName.equals(attendee.emergencyContactFullName) : attendee.emergencyContactFullName != null)
//            return false;
//        if (emergencyContactPhone != null ? !emergencyContactPhone.equals(attendee.emergencyContactPhone) : attendee.emergencyContactPhone != null)
//            return false;
//        if (parentIsEmergencyContact != null ? !parentIsEmergencyContact.equals(attendee.parentIsEmergencyContact) : attendee.parentIsEmergencyContact != null)
//            return false;
//        if (parentFullName != null ? !parentFullName.equals(attendee.parentFullName) : attendee.parentFullName != null)
//            return false;
//        if (parentPhone != null ? !parentPhone.equals(attendee.parentPhone) : attendee.parentPhone != null)
//            return false;
//        if (parentFormReceived != null ? !parentFormReceived.equals(attendee.parentFormReceived) : attendee.parentFormReceived != null)
//            return false;
//        if (paid != null ? !paid.equals(attendee.paid) : attendee.paid != null) return false;
//        if (paidAmount != null ? !paidAmount.equals(attendee.paidAmount) : attendee.paidAmount != null) return false;
//        if (compedBadge != null ? !compedBadge.equals(attendee.compedBadge) : attendee.compedBadge != null)
//            return false;
////        if (badge != null ? !badge.equals(attendee.badge) : attendee.badge != null) return false;
//        if (order != null ? !order.equals(attendee.order) : attendee.order != null) return false;
//        if (checkedIn != null ? !checkedIn.equals(attendee.checkedIn) : attendee.checkedIn != null) return false;
//        if (checkInTime != null ? !checkInTime.equals(attendee.checkInTime) : attendee.checkInTime != null)
//            return false;
//        return true;
//    }
}

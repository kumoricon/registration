package org.kumoricon.registration.model.inlineregistration;

import java.time.LocalDate;

public class InLineRegistration {
    private Long id;
    private String firstName;
    private String lastName;
    private String legalFirstName;
    private String legalLastName;
    private Boolean nameIsLegalName;
    private String preferredPronoun;
    private String zip;
    private String country;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private String emergencyContactFullName;
    private String emergencyContactPhone;
    private Boolean parentIsEmergencyContact;
    private String parentFullName;
    private String parentPhone;
    private String registrationNumber;
    private String membershipType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InLineRegistration inLineReg = (InLineRegistration) o;

        return id.equals(inLineReg.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        if (id != null) {
            return String.format("[InLineReg %s: %s %s]", id, firstName, lastName);
        } else {
            return String.format("[InLineReg: %s %s]", firstName, lastName);
        }
    }
}

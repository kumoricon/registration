package org.kumoricon.registration.inlinereg.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InLineRegistrationAttendee {
    private final String uuid;
    private final String firstName;
    private final String lastName;
    private final Boolean nameOnIdSame;
    private final String firstNameOnId;
    private final String lastNameOnId;
    private final Integer birthYear;
    private final Integer birthMonth;
    private final Integer birthDay;
    private final String email;
    private final String phone;
    private final String postal;
    private final String country;
    private final String emergencyName;
    private final String emergencyPhone;
    private final Boolean parentContactSeparate;
    private final String parentName;
    private final String parentPhone;
    private final String pronouns;
    private final String membershipType;

    @JsonCreator
    public InLineRegistrationAttendee(@JsonProperty(value = "uuid", required = true) String uuid,
                                      @JsonProperty(value = "firstName", required = true) String firstName,
                                      @JsonProperty(value = "lastName", required = true) String lastName,
                                      @JsonProperty(value = "nameOnIdSame", required = true) Boolean nameOnIdSame,
                                      @JsonProperty(value = "firstNameOnId") String firstNameOnId,
                                      @JsonProperty(value = "lastNameOnId") String lastNameOnId,
                                      @JsonProperty(value = "birthYear", required = true) Integer birthYear,
                                      @JsonProperty(value = "birthMonth", required = true) Integer birthMonth,
                                      @JsonProperty(value = "birthDay", required = true) Integer birthDay,
                                      @JsonProperty(value = "email", required = true) String email,
                                      @JsonProperty(value = "phone", required = true) String phone,
                                      @JsonProperty(value = "postal", required = true) String postal,
                                      @JsonProperty(value = "country", required = true) String country,
                                      @JsonProperty(value = "emergencyName", required = true) String emergencyName,
                                      @JsonProperty(value = "emergencyPhone", required = true) String emergencyPhone,
                                      @JsonProperty(value = "parentContactSeparate", required = true) Boolean parentContactSeparate,
                                      @JsonProperty(value = "parentName") String parentName,
                                      @JsonProperty(value = "parentPhone") String parentPhone,
                                      @JsonProperty(value = "pronouns", required = true) String pronouns,
                                      @JsonProperty(value = "membershipType", required = true) String membershipType) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nameOnIdSame = nameOnIdSame;
        this.firstNameOnId = firstNameOnId;
        this.lastNameOnId = lastNameOnId;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.email = email;
        this.phone = phone;
        this.postal = postal;
        this.country = country;
        this.emergencyName = emergencyName;
        this.emergencyPhone = emergencyPhone;
        this.parentContactSeparate = parentContactSeparate;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.pronouns = pronouns;
        this.membershipType = membershipType;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Boolean getNameOnIdSame() {
        return nameOnIdSame;
    }

    public String getFirstNameOnId() {
        return firstNameOnId;
    }

    public String getLastNameOnId() {
        return lastNameOnId;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public Integer getBirthDay() {
        return birthDay;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPostal() {
        return postal;
    }

    public String getCountry() {
        return country;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public Boolean getParentContactSeparate() {
        return parentContactSeparate;
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public String getPronouns() {
        return pronouns;
    }

    public String getMembershipType() {
        return membershipType;
    }
}


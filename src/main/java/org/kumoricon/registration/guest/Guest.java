package org.kumoricon.registration.guest;

import java.time.LocalDate;

public class Guest {
    private Integer id;
    private String onlineId;
    private String firstName;
    private String lastName;
    private String legalFirstName;
    private String legalLastName;
    private String preferredPronoun;
    private String fanName;
    private LocalDate birthDate;
    private Boolean hasBadgeImage;
    private String badgeImageFileType;
    private String ageCategoryAtCon;
    private String badgeNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(String onlineId) {
        this.onlineId = onlineId;
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

    public String getPreferredPronoun() { return preferredPronoun; }

    public void setPreferredPronoun(String preferredPronoun) { this.preferredPronoun = preferredPronoun; }

    public String getFanName() {
        if (fanName == null) return "";
        return fanName;
    }

    public void setFanName(String fanName) {
        this.fanName = fanName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthdate) {
        this.birthDate = birthdate;
    }

    public Boolean getHasBadgeImage() {
        return hasBadgeImage;
    }

    public void setHasBadgeImage(Boolean hasBadgeImage) {
        this.hasBadgeImage = hasBadgeImage;
    }

    public String getBadgeImageFileType() {
        return badgeImageFileType;
    }

    public void setBadgeImageFileType(String badgeImageFileType) {
        this.badgeImageFileType = badgeImageFileType;
    }

    public String getBadgeNumber() { return badgeNumber; }

    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }

    public String getName() {
        return (firstName + " "+ lastName).trim();
    }

    public String getLegalName() {
        return (legalFirstName + " " + legalLastName).trim();
    }

    public String getAgeCategoryAtCon() {
        return ageCategoryAtCon;
    }

    public void setAgeCategoryAtCon(String ageCategoryAtCon) {
        this.ageCategoryAtCon = ageCategoryAtCon;
    }

    @Override
    public String toString() {
        return "[Guest " + id +
                ": " + getName() + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Guest guest = (Guest) o;

        return id.equals(guest.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}

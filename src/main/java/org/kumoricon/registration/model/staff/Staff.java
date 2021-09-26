package org.kumoricon.registration.model.staff;


import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class Staff {
    private Integer id;

    private String uuid;
    private String firstName;
    private String lastName;
    private String legalFirstName;
    private String legalLastName;
    private String preferredPronoun;
    private String shirtSize;
    private String department;
    private String departmentColorCode;

    private List<String> positions;
    private Boolean suppressPrintingDepartment;
    private Boolean checkedIn;
    private LocalDate birthDate;
    private String ageCategoryAtCon;
    private Boolean deleted;
    private Boolean hasBadgeImage;
    private String badgeImageFileType;
    private OffsetDateTime checkedInAt;
    private Long lastModifiedMS;
    private Boolean badgePrinted;
    private Integer badgePrintCount;
    private Boolean informationVerified;
    private Boolean pictureSaved;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getShirtSize() {
        return shirtSize;
    }

    public void setShirtSize(String shirtSize) {
        this.shirtSize = shirtSize;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentColorCode() {
        return departmentColorCode;
    }

    public void setDepartmentColorCode(String departmentColorCode) {
        this.departmentColorCode = departmentColorCode;
    }

    public List<String> getPositions() {
        return positions;
    }

    public void setPositions(List<String> positions) {
        this.positions = positions;
    }

    public Boolean getSuppressPrintingDepartment() {
        return suppressPrintingDepartment;
    }

    public void setSuppressPrintingDepartment(Boolean suppressPrintingDepartment) {
        this.suppressPrintingDepartment = suppressPrintingDepartment;
    }

    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAgeCategoryAtCon() {
        return ageCategoryAtCon;
    }

    public void setAgeCategoryAtCon(String ageCategoryAtCon) {
        this.ageCategoryAtCon = ageCategoryAtCon;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public OffsetDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(OffsetDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public Long getLastModifiedMS() {
        return lastModifiedMS;
    }

    public void setLastModifiedMS(Long lastModifiedMS) {
        this.lastModifiedMS = lastModifiedMS;
    }

    public Boolean getBadgePrinted() {
        return badgePrinted;
    }

    public void setBadgePrinted(Boolean badgePrinted) {
        this.badgePrinted = badgePrinted;
    }

    public Integer getBadgePrintCount() {
        return badgePrintCount;
    }

    public void setBadgePrintCount(Integer badgePrintCount) {
        this.badgePrintCount = badgePrintCount;
    }

    public Boolean getInformationVerified() {
        return informationVerified;
    }

    public void setInformationVerified(Boolean informationVerified) {
        this.informationVerified = informationVerified;
    }

    public Boolean getPictureSaved() {
        return pictureSaved;
    }

    public void setPictureSaved(Boolean pictureSaved) {
        this.pictureSaved = pictureSaved;
    }

    @Override
    public String toString() {
        return "[Staff " + id + ": " + firstName + " " + lastName + "]";
    }
}

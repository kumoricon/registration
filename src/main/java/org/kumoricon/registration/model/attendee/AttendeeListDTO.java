package org.kumoricon.registration.model.attendee;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class AttendeeListDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String legalFirstName;
    private String legalLastName;
    private String fanName;
    private LocalDate birthDate;
    private String badgeType;
    private Boolean checkedIn;
    private Instant checkInTime;
    private Integer orderId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getLegalFirstName() { return legalFirstName; }
    public void setLegalFirstName(String legalFirstName) { this.legalFirstName = legalFirstName; }

    public String getLegalLastName() { return legalLastName; }
    public void setLegalLastName(String legalLastName) { this.legalLastName = legalLastName; }

    public String getFanName() { return fanName; }
    public void setFanName(String fanName) { this.fanName = fanName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getBadgeType() { return badgeType; }
    public void setBadgeType(String badgeType) { this.badgeType = badgeType; }

    public Boolean getCheckedIn() { return checkedIn; }
    public void setCheckedIn(Boolean checkedIn) { this.checkedIn = checkedIn; }

    public Instant getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Instant checkInTime) { this.checkInTime = checkInTime; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Long getAge() {
        if (birthDate == null) { return 0L; }
        LocalDate now = LocalDate.now(ZoneId.of("America/Los_Angeles"));
        return ChronoUnit.YEARS.between(birthDate, now);
    }
}

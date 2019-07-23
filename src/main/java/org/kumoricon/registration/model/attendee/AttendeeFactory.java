package org.kumoricon.registration.model.attendee;

import org.kumoricon.registration.model.badge.Badge;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class AttendeeFactory {
    private final ZoneId timezone;

    public AttendeeFactory() {
        this.timezone = ZoneId.of("America/Los_Angeles");
    }

    public Attendee generateDemoAttendee(Badge badge) {
        Attendee attendee = new Attendee();
        attendee.setFirstName("Firstname");
        attendee.setLastName("Lastname");
        attendee.setPreferredPronoun("They");
        attendee.setFanName("Fan Name - Adult");
        attendee.setBadgeNumber("TST12340");
        attendee.setBadge(badge);
        attendee.setCountry("United States of America");
        attendee.setZip("97201");
        attendee.setPhoneNumber("123-123-1234");
        attendee.setCheckedIn(true);
        attendee.setEmergencyContactFullName("Mom");
        attendee.setEmergencyContactPhone("321-321-4321");
        attendee.setBirthDate(LocalDate.now(timezone).minusYears(30L));
        attendee.setPaid(true);
        attendee.setPaidAmount(attendee.getBadge().getCostForAge(attendee.getAge()));
        return attendee;
    }

    public Attendee generateYouthAttendee(Badge badge) {
        Attendee attendee = generateDemoAttendee(badge);
        attendee.setFirstName("Firstname");
        attendee.setFanName("Fan Name - Youth");
        attendee.setBadgeNumber("TST12341");
        attendee.setBirthDate(LocalDate.now(timezone).minusYears(13L));
        attendee.setParentFullName(attendee.getEmergencyContactFullName());
        attendee.setParentPhone(attendee.getEmergencyContactPhone());
        attendee.setParentIsEmergencyContact(true);
        attendee.setPaidAmount(attendee.getBadge().getCostForAge(attendee.getAge()));
        return attendee;
    }

    public Attendee generateChildAttendee(Badge badge) {
        Attendee attendee = generateDemoAttendee(badge);
        attendee.setFirstName("Firstname");
        attendee.setFanName("Fan Name - Child");
        attendee.setBirthDate(LocalDate.now(timezone).minusYears(7L));
        attendee.setBadgeNumber("TST12342");
        attendee.setPaidAmount(attendee.getBadge().getCostForAge(attendee.getAge()));
        return attendee;
    }

}

package org.kumoricon.registration.inlinereg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kumoricon.registration.exceptions.NotFoundException;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.inlineregistration.InLineRegRepository;
import org.kumoricon.registration.model.inlineregistration.InLineRegistration;
import org.kumoricon.registration.model.order.OrderService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InLineRegistrationServiceTest {

    @Mock(lenient = true)
    private InLineRegRepository inLineRegRepository;

    @Mock(lenient = true)
    private OrderService orderService;

    @Mock(lenient = true)
    private BadgeService badgeService;

    private InLineRegistrationService inLineRegistrationService;

    @BeforeEach
    public void setUp() {
        this.inLineRegistrationService = spy(new InLineRegistrationService(inLineRegRepository, orderService, badgeService));
    }

    @Test
    void attendeeFromInLineRegDefaultValues() {
        // These fields should be set by default
        when(badgeService.findIdByBadgeName("weekend")).thenReturn(1);
        InLineRegistration ilr = buildIlr("weekend");
        Attendee a = inLineRegistrationService.attendeeFromInLineReg(ilr);
        assertFalse(a.getCheckedIn());
        assertNull(a.getCheckInTime());
        assertFalse(a.isBadgePrinted());
        assertFalse(a.isBadgePrePrinted());
        assertFalse(a.getCompedBadge());
        assertFalse(a.getPaid());
    }

    @Test
    void attendeeFromInLineRegWeekend() {
        when(badgeService.findIdByBadgeName("weekend")).thenReturn(1);
        InLineRegistration ilr = buildIlr("weekend");
        Attendee a = inLineRegistrationService.attendeeFromInLineReg(ilr);
        assertEquals(1, a.getBadgeId());
        assertEquals("Some", a.getFirstName());
        assertEquals("Body", a.getLastName());
        assertEquals("test@example.com", a.getEmail());
        assertEquals("111-111-1111", a.getPhoneNumber());
        assertEquals(true, a.getNameIsLegalName());
        assertEquals("He/Him", a.getPreferredPronoun());
        assertEquals("USA", a.getCountry());
        assertEquals(LocalDate.of(2000, 5, 1), a.getBirthDate());
        assertEquals("Mom", a.getEmergencyContactFullName());
        assertEquals("123-123-1234", a.getEmergencyContactPhone());
    }

    @Test
    void attendeeFromInLineRegDayBadge() {
        // Mock the call to findBadgeNameForToday() because it will return "Saturday" or "Sunday"
        // on the weekend, and "Friday" every other day. Otherwise this test would fail on the weekend
        when(inLineRegistrationService.findBadgeNameForToday()).thenReturn("friday");
        when(badgeService.findIdByBadgeName("friday")).thenReturn(2);
        InLineRegistration ilr = buildIlr("day");
        Attendee a = inLineRegistrationService.attendeeFromInLineReg(ilr);
        assertEquals(2, a.getBadgeId());
    }

    @Test
    void attendeeFromInLineRegMissingBadge() {
        when(badgeService.findIdByBadgeName("missing")).thenThrow(new NotFoundException("Badge named missing not found"));
        InLineRegistration ilr = buildIlr("missing");
        Assertions.assertThrows(NotFoundException.class, () -> {
            Attendee a = inLineRegistrationService.attendeeFromInLineReg(ilr);
        });
    }

    private InLineRegistration buildIlr(String membershipType) {
        InLineRegistration ilr = new InLineRegistration();
        ilr.setFirstName("Some");
        ilr.setLastName("Body");
        ilr.setConfirmationCode("ABC123");
        ilr.setEmail("test@example.com");
        ilr.setPhoneNumber("111-111-1111");
        ilr.setBirthDate(LocalDate.of(2000, 5, 1));
        ilr.setEmergencyContactFullName("Mom");
        ilr.setEmergencyContactPhone("123-123-1234");
        ilr.setNameIsLegalName(true);
        ilr.setPreferredPronoun("He/Him");
        ilr.setZip("12345");
        ilr.setCountry("USA");
        ilr.setMembershipType(membershipType);
        return ilr;
    }
}
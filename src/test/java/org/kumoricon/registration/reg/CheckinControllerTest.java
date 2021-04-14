package org.kumoricon.registration.reg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.blacklist.BlacklistRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.kumoricon.registration.print.BadgePrintService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import javax.print.PrintException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class CheckinControllerTest {

    @Mock(lenient = true)
    AttendeeRepository attendeeRepository;

    @Mock(lenient = true)
    AttendeeHistoryRepository attendeeHistoryRepository;

    @Mock(lenient = true)
    BadgePrintService badgePrintService;

    @Mock(lenient = true)
    BlacklistRepository blacklistRepository;

    Map<Integer, Attendee> attendees = new HashMap<>();

    CheckinController controller;
    User user;

    UserService userService;

    @BeforeEach
    public void setUp() {
        this.controller = new CheckinController(attendeeRepository,
                attendeeHistoryRepository,
                new AttendeeService(attendeeRepository, attendeeHistoryRepository, blacklistRepository),
                badgePrintService, userService);

        attendees.put(1, buildTestAttendee(1, "Some", "Body", 18));
        attendees.put(2, buildTestAttendee(2, "Youth", "Bodyson", 15));

        when(attendeeRepository.findById(eq(1))).thenReturn(attendees.get(1));
        when(attendeeRepository.findById(eq(2))).thenReturn(attendees.get(2));
        when(attendeeHistoryRepository.findAllDTObyAttendeeId(eq(1))).thenReturn(List.of());
        user = buildUser();
    }

    @Test
    public void verifyDataHappyPath() {
        ConcurrentModel model = new ConcurrentModel();
        String template = controller.verifyData(model, 1);
        assertEquals("reg/checkin-id", template);
        assertEquals((Integer)1, ((Attendee)model.getAttribute("attendee")).getId());
        assertEquals(0, ((List<AttendeeHistoryDTO>) model.getAttribute("history")).size());
    }

    @Test
    public void checkInHappyPathAdult() {
        String template = controller.checkIn(1, null, user, "test|0|0");
        assertTrue(template.startsWith("redirect:/reg/checkin/1/printbadge?msg="));
    }

    @Test
    public void checkInHappyPathAdultBadgePrePrinted() {
        attendees.get(1).setBadgePrePrinted(true);
        String template = controller.checkIn(1, null, user, "test|0|0");
        assertEquals("redirect:/reg/checkin/1/printbadge?err=Badge+is+pre-+printed", template);
    }

    @Test
    public void checkInHappyPathYouth() {
        String template = controller.checkIn(2, true, user, "test|0|0");
        assertTrue(template.startsWith("redirect:/reg/checkin/2/printbadge?msg="));
    }

    @Test
    public void checkInMinorFailsWithNullConsentForm() {
        assertThrows(RuntimeException.class, () -> controller.checkIn(2, null, user, "test|0|0"));
    }

    @Test
    public void checkInMinorFailsWithoutConsentForm() {
        assertThrows(RuntimeException.class, () -> controller.checkIn(2, false, user, "test|0|0"));
    }

    @Test
    public void checkInBadgePrintingFailed() throws PrintException {
        when(badgePrintService.printBadgesForAttendees(any(), any())).thenThrow(new PrintException("Printer Error"));
        String template = controller.checkIn(2, true, user, "test|0|0");
        assertEquals("redirect:/reg/checkin/2/printbadge?err=Printer Error", template);
    }


    @Test
    public void printBadgeAttendeeNotCheckedInRedirects() {
        attendees.get(1).setCheckedIn(false);
        assertEquals("redirect:/reg/checkin/1?err=attendee+not+checked+in", controller.printBadge(null, 1));

    }

    @Test
    public void printBadgeHappyPath() {
        attendees.get(1).setCheckedIn(true);
        Model model = new ConcurrentModel();
        assertEquals("reg/checkin-id-printbadge", controller.printBadge(model, 1));
        assertEquals(attendees.get(1), model.getAttribute("attendee"));
    }


    @Test
    public void printBadgeActionBadgePrinted() {
        attendees.get(1).setCheckedIn(true);
        String template = controller.printBadgeAction(1, "badgePrintedSuccessfully", "test|0|0");
        assertEquals("redirect:/search?msg=Checked+in+&orderId=1", template);
        Attendee saved = attendees.get(1);
        assertTrue(saved.isBadgePrinted());
    }

    @Test
    public void printBadgeActionReprintBadge() throws PrintException {
        when(badgePrintService.printBadgesForAttendees(any(), any())).thenReturn("badge printed successfully");

        attendees.get(1).setCheckedIn(true);
        String template = controller.printBadgeAction(1, "reprintDuringCheckin", "test|0|0");
        assertEquals("redirect:/reg/checkin/1/printbadge?msg=badge printed successfully", template);
    }

    @Test
    public void printBadgeActionReprintBadgePrintError() throws PrintException {
        when(badgePrintService.printBadgesForAttendees(any(), any())).thenThrow(new PrintException("Error printing"));

        attendees.get(1).setCheckedIn(true);
        String template = controller.printBadgeAction(1, "reprintDuringCheckin", "test|0|0");
        assertEquals("redirect:/reg/checkin/1/printbadge?err=Error printing", template);
    }

    private Attendee buildTestAttendee(Integer id, String firstName, String lastName, Integer age) {
        Attendee attendee = new Attendee();
        attendee.setId(id);
        attendee.setFirstName(firstName);
        attendee.setLastName(lastName);
        attendee.setLegalFirstName(firstName);
        attendee.setLegalLastName(lastName);
        attendee.setNameIsLegalName(true);
        attendee.setBirthDate(LocalDate.now().minusYears(age));
        attendee.setOrderId(1);
        return attendee;
    }

    private User buildUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("staff");
        user.setLastBadgeNumberCreated(1000);
        return user;
    }
}
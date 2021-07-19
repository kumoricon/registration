package org.kumoricon.registration.inlinereg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.kumoricon.registration.exceptions.NotFoundException;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.inlineregistration.InLineRegRepository;
import org.kumoricon.registration.model.inlineregistration.InLineRegistration;
import org.kumoricon.registration.model.order.OrderService;
import org.kumoricon.registration.model.user.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class InLineRegistrationControllerTest {

    @Mock(lenient = true)
    private InLineRegRepository inLineRegRepository;

    @Mock(lenient = true)
    private OrderService orderService;

    private InLineRegistrationController inLineRegistrationController;

    @BeforeEach
    public void setUp() {
        this.inLineRegistrationController = new InLineRegistrationController(
                new InLineRegistrationService(inLineRegRepository, orderService));
    }

    @Test
    public void inLineCheckInSearch() {
        Model model = new ConcurrentModel();
        String template = inLineRegistrationController.inLineCheckIn(model, null, null);
        assertEquals("inlinereg/search", template);
        assertNull(model.getAttribute("name"));
    }

    @Test
    public void inLineCheckInSearchByNameMatches() {
        Model model = new ConcurrentModel();
        when(inLineRegRepository.findByNameLike("Person")).thenReturn(buildTestData());

        String template = inLineRegistrationController.inLineCheckIn(model, "Person", null);
        assertEquals("inlinereg/search", template);
        assertEquals("Person", model.getAttribute("name"));
        Map<String, List<InLineRegistration>> results = (Map<String, List<InLineRegistration>>) model.getAttribute("results");
        assertTrue(null != results);
        assertEquals(1, results.size());
    }

    @Test
    public void inLineCheckInPostNoCodeFound() {
        // Attempting to create an order returns an error when no registrations are found with
        // that code
        UUID randomId = UUID.randomUUID();
        Model model = new ConcurrentModel();
        when(inLineRegRepository.findByConfirmationCode(any())).thenThrow(new NotFoundException("Code Not Found"));

        String template = inLineRegistrationController.inLineCheckInPost(model, randomId, buildUser());
        assertEquals("inlinereg/search", template);
        assertEquals("No attendees found for orderUuid " + randomId, model.getAttribute("err"));
    }


    @Test
    public void inLineCheckInPost() {
        UUID randomOrderId = UUID.randomUUID();
        // Happy path, creating attendees and an order for attendees who have registered in line
        Model model = new ConcurrentModel();
        ArgumentCaptor<Attendee> attendeeCaptor = ArgumentCaptor.forClass(Attendee.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        List<InLineRegistration> inLineData = buildTestData();
        when(inLineRegRepository.findByOrderUuid(randomOrderId)).thenReturn(inLineData);
        when(orderService.saveNewOrderForUser(any())).thenReturn(10);

        String template = inLineRegistrationController.inLineCheckInPost(model, randomOrderId, buildUser());
        assertEquals("redirect:/reg/atconorder/10", template);

        // Make sure createOrder was called
        verify(orderService, times(1)).saveNewOrderForUser(userCaptor.capture());
        User user = userCaptor.getValue();
        assertEquals((Integer) 1, user.getId());

        // Make sure Attendees were saved with all data from InLine Registration
        verify(orderService, times(2)).saveAttendeeToOrder(any(), attendeeCaptor.capture(), any());
        List<Attendee> attendees = attendeeCaptor.getAllValues();

        assertTrue(dtosAreSame(inLineData.get(0), attendees.get(0)));
        assertTrue(dtosAreSame(inLineData.get(1), attendees.get(1)));
    }

    private List<InLineRegistration> buildTestData() {
        InLineRegistration adult = buildInLineRegistration("Some", "Person", LocalDate.now().minusYears(18));
        InLineRegistration youth = buildInLineRegistration("Another", "Person", LocalDate.now().minusYears(14));
        youth.setParentFullName("Mom");
        youth.setParentPhone("123-123-1234");
        youth.setParentIsEmergencyContact(true);
        return List.of(adult, youth);
    }

    private InLineRegistration buildInLineRegistration(String firstName, String lastName, LocalDate birthDate) {
        InLineRegistration ilr = new InLineRegistration();
        ilr.setFirstName(firstName);
        ilr.setLastName(lastName);
        ilr.setLegalFirstName(firstName);
        ilr.setLegalLastName(lastName);
        ilr.setNameIsLegalName(true);
        ilr.setConfirmationCode("ABC123");
        ilr.setBirthDate(birthDate);
        ilr.setCountry("United States of America");
        ilr.setEmergencyContactFullName("Mom");
        ilr.setEmergencyContactPhone("123-123-1234");
        ilr.setMembershipType("Weekend");
        ilr.setZip("12345");
        return ilr;
    }

    private User buildUser() {
        User user = new User();
        user.setUsername("admin");
        user.setEnabled(true);
        user.setLastBadgeNumberCreated(1000);
        user.setId(1);
        return user;
    }

    private boolean dtosAreSame(InLineRegistration ilr, Attendee attendee) {
        if (!Objects.equals(ilr.getFirstName(), attendee.getFirstName())) return false;
        if (!Objects.equals(ilr.getLastName(), attendee.getLastName())) return false;
        if (!Objects.equals(ilr.getLegalFirstName(), attendee.getLegalFirstName())) return false;
        if (!Objects.equals(ilr.getLegalLastName(), attendee.getLegalLastName())) return false;
        if (!Objects.equals(ilr.getNameIsLegalName(), attendee.getNameIsLegalName())) return false;
        if (!Objects.equals(ilr.getBirthDate(), attendee.getBirthDate())) return false;
        if (!Objects.equals(ilr.getCountry(), attendee.getCountry())) return false;
        if (!Objects.equals(ilr.getEmail(), attendee.getEmail())) return false;
        if (!Objects.equals(ilr.getPhoneNumber(), attendee.getPhoneNumber())) return false;
        if (!Objects.equals(ilr.getZip(), attendee.getZip())) return false;
        if (!Objects.equals(ilr.getEmergencyContactFullName(), attendee.getEmergencyContactFullName())) return false;
        if (!Objects.equals(ilr.getEmergencyContactPhone(), attendee.getEmergencyContactPhone())) return false;
        if (!Objects.equals(ilr.getParentFullName(), attendee.getParentFullName())) return false;
        if (!Objects.equals(ilr.getParentIsEmergencyContact(), attendee.getParentIsEmergencyContact())) return false;
        if (!Objects.equals(ilr.getPreferredPronoun(), attendee.getPreferredPronoun())) return false;
        return true;
    }
}
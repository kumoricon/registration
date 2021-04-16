package org.kumoricon.registration.inlinereg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.inlineregistration.InLineRegRepository;
import org.kumoricon.registration.model.inlineregistration.InLineRegistration;
import org.kumoricon.registration.model.order.OrderService;
import org.kumoricon.registration.model.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InLineRegistrationService {
    private final InLineRegRepository inLineRegRepository;
    private final OrderService orderService;

    public InLineRegistrationService(InLineRegRepository inLineRegRepository,
                                     OrderService orderService) {
        this.inLineRegRepository = inLineRegRepository;
        this.orderService = orderService;
    }

    @Transactional
    public int createOrder(String regCode, User user) {
        List<InLineRegistration> inLineRegistrations = inLineRegRepository.findByConfirmationCode(regCode);

        Integer orderId = orderService.saveNewOrderForUser(user);

        for (InLineRegistration ilr : inLineRegistrations) {
            Attendee attendee = attendeeFromInLineReg(ilr);
            orderService.saveAttendeeToOrder(orderId, attendee, user);
            // TODO: Add a function that would save a list of attendees to a given order? Then again, orders
            // usually won't have more than 2-4 people on them, so a few database round trips won't hurt.
        }

        return orderId;
    }

    @Transactional(readOnly = true)
    public Map<String, List<InLineRegistration>> findMatchingByName(String name) {
        List<InLineRegistration> ilr = inLineRegRepository.findByNameLike(name);

        Map<String, List<InLineRegistration>> output = new HashMap<>();
        for (InLineRegistration i : ilr) {
            if (!output.containsKey(i.getConfirmationCode())) {
                output.put(i.getConfirmationCode(), new ArrayList<>());
            }
            output.get(i.getConfirmationCode()).add(i);
        }
        return output;
    }


    private Attendee attendeeFromInLineReg(InLineRegistration ilr) {
        Attendee attendee = new Attendee();
        attendee.setFirstName(ilr.getFirstName());
        attendee.setLastName(ilr.getLastName());
        attendee.setLegalFirstName(ilr.getLegalFirstName());
        attendee.setLegalLastName(ilr.getLegalLastName());
        attendee.setNameIsLegalName(ilr.getNameIsLegalName());
        attendee.setPreferredPronoun(ilr.getPreferredPronoun());
        attendee.setBadgePrePrinted(false);
        attendee.setBadgePrinted(false);
        attendee.setBirthDate(ilr.getBirthDate());
        attendee.setCheckedIn(false);
        attendee.setCompedBadge(false);
        attendee.setCountry(ilr.getCountry());
        attendee.setEmail(ilr.getEmail());
        attendee.setEmergencyContactFullName(ilr.getEmergencyContactFullName());
        attendee.setEmergencyContactPhone(ilr.getEmergencyContactPhone());
        attendee.setPaid(false);
        attendee.setParentFullName(ilr.getParentFullName());
        attendee.setParentPhone(ilr.getParentPhone());
        attendee.setParentIsEmergencyContact(ilr.getParentIsEmergencyContact());
        attendee.setZip(ilr.getZip());

        attendee.setBadgeId(1);     // TODO: find type based on imported info? Not sure if that will be a thing

        return attendee;
    }

}


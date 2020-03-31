package org.kumoricon.registration.inlinereg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeService;
import org.kumoricon.registration.model.inlineregistration.InLineRegRepository;
import org.kumoricon.registration.model.inlineregistration.InLineRegistration;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.OrderService;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InLineRegistrationService {
    private final InLineRegRepository inLineRegRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final AttendeeService attendeeService;
    private static final Logger log = LoggerFactory.getLogger(InLineRegistrationService.class);

    public InLineRegistrationService(InLineRegRepository inLineRegRepository,
                                     OrderRepository orderRepository,
                                     OrderService orderService,
                                     AttendeeService attendeeService) {
        this.inLineRegRepository = inLineRegRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.attendeeService = attendeeService;
    }


    @Transactional
    public int createOrder(String regCode, User user) throws InLineRegistrationException {
        List<InLineRegistration> inLineRegistrations = inLineRegRepository.findByRegistrationNumber(regCode);
        if (inLineRegistrations.size() == 0) {
            throw new InLineRegistrationException("No attendees found for code " + regCode);
        }

        Integer orderId = orderService.saveNewOrderForUser(user);

        for (InLineRegistration ilr : inLineRegistrations) {
            Attendee attendee = attendeeFromInLineReg(ilr);
            orderService.saveAttendeeToOrder(orderId, attendee, user);
            // TODO: Add a function that would save a list of attendees to a given order? Then again, orders
            // usually won't have more than 2-4 people on them, so a few database round trips won't hurt.
        }

        return orderId;
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


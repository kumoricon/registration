package org.kumoricon.registration.inlinereg;

import org.kumoricon.registration.exceptions.NotFoundException;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.inlineregistration.InLineRegRepository;
import org.kumoricon.registration.model.inlineregistration.InLineRegistration;
import org.kumoricon.registration.model.order.OrderService;
import org.kumoricon.registration.model.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class InLineRegistrationService {
    private final InLineRegRepository inLineRegRepository;
    private final OrderService orderService;
    private final BadgeService badgeService;
    public InLineRegistrationService(InLineRegRepository inLineRegRepository,
                                     OrderService orderService,
                                     BadgeService badgeService) {
        this.inLineRegRepository = inLineRegRepository;
        this.orderService = orderService;
        this.badgeService = badgeService;
    }

    @Transactional
    public int createOrder(UUID orderUuid, User user) {
        List<InLineRegistration> inLineRegistrations = inLineRegRepository.findByOrderUuid(orderUuid);

        if (inLineRegistrations.size() == 0) {
            throw new NotFoundException("No attendees found for orderUuid " + orderUuid);
        }

        Integer orderId = orderService.saveNewOrderForUser(user);

        for (InLineRegistration ilr : inLineRegistrations) {
            Attendee attendee = attendeeFromInLineReg(ilr);
            orderService.saveAttendeeToOrder(orderId, attendee, user);
        }

        return orderId;
    }

    @Transactional(readOnly = true)
    public Map<UUID, List<InLineRegistration>> findMatchingBySearch(String search) {
        List<InLineRegistration> ilrName = inLineRegRepository.findByNameLike(search);
        List<InLineRegistration> ilrCode = inLineRegRepository.findByConfirmationCode(search);

        Map<UUID, List<InLineRegistration>> output = new HashMap<>();
        for(InLineRegistration i : ilrName) {
            if (!output.containsKey(i.getOrderUuid())) {
                output.put(i.getOrderUuid(), new ArrayList<>());
            }
            output.get(i.getOrderUuid()).add(i);
        }

        for(InLineRegistration i : ilrCode) {
            if (!output.containsKey(i.getOrderUuid())) {
                output.put(i.getOrderUuid(), new ArrayList<>());
            }
            output.get(i.getOrderUuid()).add(i);
        }
        return output;
    }

    protected Attendee attendeeFromInLineReg(InLineRegistration ilr) {
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
        attendee.setPhoneNumber(ilr.getPhoneNumber());
        attendee.setEmergencyContactFullName(ilr.getEmergencyContactFullName());
        attendee.setEmergencyContactPhone(ilr.getEmergencyContactPhone());
        attendee.setPaid(false);
        attendee.setParentFullName(ilr.getParentFullName());
        attendee.setParentPhone(ilr.getParentPhone());
        attendee.setParentIsEmergencyContact(ilr.getParentIsEmergencyContact());
        attendee.setZip(ilr.getZip());
        attendee.setBadgeId(findBadgeIdByMembershipType(ilr.getMembershipType()));
        return attendee;
    }

    protected Integer findBadgeIdByMembershipType(String membershipType) {
        if (membershipType.equalsIgnoreCase("day")) {
            return badgeService.findIdByBadgeName(findBadgeNameForToday());
        } else {
            return badgeService.findIdByBadgeName(membershipType);
        }
    }

    /**
     * Returns today's name if it's on a day when Kumoricon is open, or Friday otherwise.
     * TODO: This is hard coded for now, but another way to do it would be to check for existing day badge types
     * in case we ever have day badges on Thursday.
     *
     * @return "FRIDAY", "SATURDAY", or "SUNDAY"
     */
    protected String findBadgeNameForToday() {
        String today = ZonedDateTime.now(ZoneId.of("America/Los_Angeles")).getDayOfWeek().name();
        if (today.equalsIgnoreCase("saturday") || today.equalsIgnoreCase("sunday")) {
            return today;
        } else {
            return "FRIDAY";
        }
    }

}
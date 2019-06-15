package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.AttendeeValidator;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.blacklist.BlacklistRepository;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.OrderService;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AtConAttendeeController {
    private final AttendeeValidator attendeeValidator;
    private final AttendeeRepository attendeeRepository;
    private final OrderRepository orderRepository;
    private final BadgeService badgeService;
    private final OrderService orderService;
    private final BlacklistRepository blacklistRepository;
    private final Logger log = LoggerFactory.getLogger(AtConRegistrationController.class);

    private static final String ATTENDEE_TEMPLATE = "reg/atcon-order-attendee";
    private static final String SPECIALTY_TEMPLATE = "reg/atcon-order-attendee-specialty";

    public AtConAttendeeController(AttendeeValidator attendeeValidator, AttendeeRepository attendeeRepository, OrderRepository orderRepository, BadgeService badgeService, OrderService orderService, BlacklistRepository blacklistRepository) {
        this.attendeeValidator = attendeeValidator;
        this.attendeeRepository = attendeeRepository;
        this.orderRepository = orderRepository;
        this.badgeService = badgeService;
        this.orderService = orderService;
        this.blacklistRepository = blacklistRepository;
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/attendee/{attendeeId}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConAttendee(Model model,
                                @PathVariable String orderId,
                                @PathVariable String attendeeId,
                                @AuthenticationPrincipal User principal,
                                @RequestParam(value = "forceValidate", defaultValue = "false", required = false) Boolean forceValidate) {
        Order order = orderRepository.findById(getIdFromParamter(orderId));
        List<Attendee> orderAttendees = attendeeRepository.findAllByOrderId(getIdFromParamter(orderId));
        Attendee attendee = null;
        if (attendeeId.equals("new")) {
            log.info("Adding new attendee to order {}", order);
            attendee = new Attendee();
            attendee.setCountry("United States of America");
            if (orderAttendees.size() > 0) {
                Attendee prevAttendee = orderAttendees.get(0);
                attendee.setEmergencyContactFullName(prevAttendee.getEmergencyContactFullName());
                attendee.setEmergencyContactPhone(prevAttendee.getEmergencyContactPhone());
                attendee.setCountry(prevAttendee.getCountry());
                if (prevAttendee.isMinor()) {
                    attendee.setParentFullName(prevAttendee.getParentFullName());
                    attendee.setParentPhone(prevAttendee.getParentPhone());
                    attendee.setParentIsEmergencyContact(prevAttendee.getParentIsEmergencyContact());
                }
            }
        } else {
            log.info("Editing attendee {} for order {}", attendeeId, order);
            attendee = attendeeRepository.findById(getIdFromParamter(attendeeId));
        }

        model.addAttribute("order", order);
        model.addAttribute("attendee", attendee);
        model.addAttribute("badgelist", badgeService.findByVisibleAndUserRight(principal));
        model.addAttribute("forceValidate", forceValidate);
        return chooseTemplate(principal, forceValidate);
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/attendee/{attendeeId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConAttendee(Model model,
                                @ModelAttribute @Validated final Attendee attendee,
                                final BindingResult bindingResult,
                                @PathVariable Integer orderId,
                                @PathVariable String attendeeId,
                                @AuthenticationPrincipal User principal,
                                @RequestParam(value = "forceValidate", defaultValue = "false", required = false) Boolean forceValidate) {

        if (bindingResult.hasErrors()) {
            Order order = orderRepository.findById(orderId);
            model.addAttribute("order", order);
            model.addAttribute("attendee", attendee);
            model.addAttribute("badgelist", badgeService.findByVisibleAndUserRight(principal));
            model.addAttribute("forceValidate", forceValidate);
            return chooseTemplate(principal, forceValidate);
        }

        // todo: add server-side validation here IF the current user doesn't have the at_con_registration_specialty
        // right. For example, check name isn't blank, etc. BUT specialty coords don't get that validation.
        // validation may be skipped if forceValidate==true AND principal.hasRight("at_con_registration_specialty"

        if (!principal.hasRight("at_con_registration_blacklist") &&
                blacklistRepository.numberOfMatches(attendee.getFirstName(), attendee.getLastName()) > 0) {
            log.info("{} matched blacklist during check in", attendee);
            return "redirect:/reg/atconorder/" + orderId + "?err=Blacklist+match.+Please+send+party+to+manager+booth";
        }

        log.info("Adding {} to order {}", attendee, orderId);
        orderService.saveAttendeeToOrder(orderId, attendee, principal);

        return "redirect:/reg/atconorder/" + orderId + "?msg=Added+" + attendee.getNameOrFanName();
    }

    private Integer getIdFromParamter(String parameter) {
        try {
            return Integer.parseInt(parameter);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Bad parameter: " + parameter + " is not an integer");
        }
    }


    /**
     * Return the correct template based on uesr permissoins and paramter override
     * @param user Current uesr
     * @param forceValidate Force validation enabled?
     * @return Template path
     */
    private String chooseTemplate(User user, Boolean forceValidate) {
        if (user != null && user.hasRight("at_con_registration_specialty")) {
            if (forceValidate) {
                return ATTENDEE_TEMPLATE;
            } else {
                return SPECIALTY_TEMPLATE;
            }
        } else {
            return ATTENDEE_TEMPLATE;
        }
    }

    @InitBinder("attendee")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(attendeeValidator);
    }


}

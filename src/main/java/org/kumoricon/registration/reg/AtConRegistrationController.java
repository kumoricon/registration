package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.OrderService;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@Controller
public class AtConRegistrationController {
    private final AttendeeRepository attendeeRepository;
    private final OrderRepository orderRepository;
    private final BadgeService badgeService;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final Logger log = LoggerFactory.getLogger(AtConRegistrationController.class);

    private static final String ATTENDEE_TEMPLATE = "reg/atcon-order-attendee";
    private static final String SPECIALTY_TEMPLATE = "reg/atcon-order-attendee-specialty";

    @Autowired
    public AtConRegistrationController(AttendeeRepository attendeeRepository,
                                       OrderRepository orderRepository,
                                       PaymentRepository paymentRepository,
                                       BadgeService badgeService,
                                       OrderService orderService) {
        this.attendeeRepository = attendeeRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.badgeService = badgeService;
        this.orderService = orderService;
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
            if (orderAttendees.size() > 0) {
                Attendee prevAttendee = orderAttendees.get(0);
                attendee.setEmergencyContactFullName(prevAttendee.getEmergencyContactFullName());
                attendee.setEmergencyContactPhone(prevAttendee.getEmergencyContactPhone());
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
                                @ModelAttribute final Attendee attendee,
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

        log.info("Adding {} to order {}", attendee, orderId);
        orderService.addAttendeeToOrder(orderId, attendee, principal);

        return "redirect:/reg/atconorder/" + orderId + "?msg=Added+" + attendee.getNameOrFanName();
    }


    @RequestMapping(value = "/reg/atconorder/{orderId}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrder(Model model, @PathVariable Integer orderId) {

        log.info("viewing order ID {}", orderId);
        Order order = orderRepository.findById(orderId);

        if (order == null) {
            throw new RuntimeException("Order ID " + orderId + " not found");
        }

        BigDecimal totalDue = orderRepository.getTotalByOrderId(orderId);
        BigDecimal totalPaid = paymentRepository.getTotalPaidForOrder(orderId);
        Boolean canComplete = !order.getPaid() && (totalDue.compareTo(totalPaid) == 0);

        model.addAttribute("order", order);
        model.addAttribute("attendees", attendeeRepository.findAllByOrderId(orderId));
        model.addAttribute("payments", paymentRepository.findByOrderId(orderId));
        model.addAttribute("canComplete", canComplete);

        return "reg/atcon-order";
    }

    @RequestMapping(value = "/reg/atconorder/new", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String newOrder(@AuthenticationPrincipal User principal) {
        log.info("creating new order");
        Order order = new Order();
        order.setOrderTakenByUser(principal);
        order.setOrderId(Order.generateOrderId());
        Integer newId = orderRepository.save(order);

        return "redirect:/reg/atconorder/" + newId + "/attendee/new";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/complete", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String orderComplete(@PathVariable Integer orderId,
                                @AuthenticationPrincipal User principal) {
        log.info("completing order {} and printing badges", orderId);
        orderService.completeOrder(orderId, principal);
        // TODO: print badges
        return "redirect:/reg/atconorder/" + orderId + "/printbadges";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/printbadges", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String printBadges(Model model,
                              @PathVariable Integer orderId) {
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);
        model.addAttribute("attendees", attendees);
        model.addAttribute("orderId", orderId);

        return "reg/atcon-order-printbadges";
    }


    @RequestMapping(value = "/reg/atconorder/{orderId}/printbadges", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String printBadgesAction(@PathVariable Integer orderId) {
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);
        log.info("printed badges successfully for orderId {}: {}", orderId, attendees);

        for (Attendee a : attendees) {
            a.setBadgePrinted(true);
        }
        attendeeRepository.saveAll(attendees);

        return "redirect:/?msg=Order%20Complete";
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

}

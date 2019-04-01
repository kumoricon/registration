package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;


@Controller
public class AtConRegistrationController {
    private final AttendeeRepository attendeeRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final BadgeService badgeService;
    private final PaymentRepository paymentRepository;
    private final String[] PAYMENT_TYPES = {"cash", "card", "checkormoneyorder"};

    @Autowired
    public AtConRegistrationController(AttendeeRepository attendeeRepository, OrderRepository orderRepository,
                                       PaymentRepository paymentRepository, UserService userService, BadgeService badgeService) {
        this.attendeeRepository = attendeeRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.badgeService = badgeService;
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/attendee/{attendeeId}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConAttendee(Model model,
                         @PathVariable String orderId,
                         @PathVariable String attendeeId) {
        Order order = orderRepository.findById(getIdFromParamter(orderId));
        List<Attendee> orderAttendees = attendeeRepository.findAllByOrderId(getIdFromParamter(orderId));
        Attendee attendee = null;
        if (attendeeId.equals("new")) {
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
            attendee = attendeeRepository.findById(getIdFromParamter(attendeeId));
        }

        model.addAttribute("order", order);
        model.addAttribute("attendee", attendee);
        model.addAttribute("badgelist", badgeService.findByVisibleTrue());
        return "reg/atcon-order-attendee";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/attendee/{attendeeId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConAttendee(Model model,
                                @ModelAttribute final Attendee attendee,
                                final BindingResult bindingResult,
                                @PathVariable String orderId,
                                @PathVariable String attendeeId,
                                Principal principal) {

        Order order = orderRepository.findById(getIdFromParamter(orderId));
        model.addAttribute("order", order);
        model.addAttribute("attendee", attendee);
        model.addAttribute("badgelist", badgeService.findByVisibleTrue());

        if (bindingResult.hasErrors()) {
            return "reg/atcon-order-attendee";
        }

        order.addToTotalAmount(attendee.getPaidAmount());
        orderRepository.save(order);
        attendee.setOrder(order);
        if (attendee.getBadgeNumber() == null) {
            String badgeNumber = userService.getNextBadgeNumber(principal.getName());
            attendee.setBadgeNumber(badgeNumber);
        }
        attendee.setPaidAmount(badgeService.getCostForBadgeType(attendee.getBadgeId(), attendee.getAge()));

        attendeeRepository.save(attendee);


        return "redirect:/reg/atconorder/" + order.getId() + "?msg=Added+" + attendee.getFirstName();
    }


    @RequestMapping(value = "/reg/atconorder/{orderId}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrder(Model model, @PathVariable Integer orderId) {

        Order order = orderRepository.findById(orderId);

        BigDecimal totalDue = orderRepository.getTotalByOrderId(orderId);
        BigDecimal totalPaid = paymentRepository.getTotalPaidForOrder(orderId);
        Boolean canComplete = totalDue.equals(totalPaid) && !order.getPaid();

        model.addAttribute("order", order);
        model.addAttribute("attendees", attendeeRepository.findAllByOrderId(orderId));
        model.addAttribute("payments", paymentRepository.findByOrderId(orderId));
        model.addAttribute("canComplete", canComplete);

        return "reg/atcon-order";
    }

    @RequestMapping(value = "/reg/atconorder/new", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String newOrder(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Order order = new Order();
        order.setOrderTakenByUser(user);
        order.setOrderId(Order.generateOrderId());
        Integer newId = orderRepository.save(order);

        return "redirect:/reg/atconorder/" + newId + "/attendee/new";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/complete", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    @Transactional
    public String orderComplete(Model model,
                                Principal principal,
                                @PathVariable Integer orderId) {
        User user = userService.findByUsername(principal.getName());
        Order order = orderRepository.findById(orderId);
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);

        order.setPaid(true);
        orderRepository.save(order);
        for (Attendee a : attendees) {
            a.setPaid(true);
            a.setCheckedIn(true);
        }

        // TODO: print badges
        // TODD: Save checkin to attendee history
        attendeeRepository.saveAll(attendees);

        return "redirect:/reg/atconorder/" + orderId + "/printbadges";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/printbadges", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String printBadges(Model model,
                              Principal principal,
                              @PathVariable Integer orderId) {
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("attendees", attendees);
        model.addAttribute("orderId", orderId);

        return "reg/atcon-order-printbadges";
    }


    @RequestMapping(value = "/reg/atconorder/{orderId}/printbadges", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String printBadgesAction(Model model,
                                Principal principal,
                                @PathVariable Integer orderId) {
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);
        User user = userService.findByUsername(principal.getName());

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

    private boolean isValidPaymentType(String paymentType) {
        for (String type : PAYMENT_TYPES) {
            if (paymentType.toLowerCase().equals(type)) {
                return true;
            }
        }
        return false;
    }

}

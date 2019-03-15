package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
public class AtConRegistrationController {
    private final AttendeeRepository attendeeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;
    private final PaymentRepository paymentRepository;
    private final String[] PAYMENT_TYPES = {"cash", "card", "checkormoneyorder"};

    @Autowired
    public AtConRegistrationController(AttendeeRepository attendeeRepository, OrderRepository orderRepository,
                                       PaymentRepository paymentRepository, UserRepository userRepository, BadgeService badgeService) {
        this.attendeeRepository = attendeeRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
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
                                @PathVariable String attendeeId) {

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
        attendeeRepository.save(attendee);


        return "redirect:/reg/atconorder/" + order.getId() + "?msg=Added+" + attendee.getFirstName();
    }


    @RequestMapping(value = "/reg/atconorder/{orderId}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrder(Model model, @PathVariable String orderId) {

        Order order = orderRepository.findById(getIdFromParamter(orderId));
        model.addAttribute("order", order);
        model.addAttribute("attendees", attendeeRepository.findAllByOrderId(getIdFromParamter(orderId)));
        model.addAttribute("payments", paymentRepository.findByOrderId(getIdFromParamter(orderId)));
        return "reg/atcon-order";
    }

    @RequestMapping(value = "/reg/atconorder/new", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String newOrder(Model model, Principal principal) {
        User user = userRepository.findOneByUsernameIgnoreCase(principal.getName());
        Order order = new Order();
        order.setOrderTakenByUser(user);
        order.setOrderId(Order.generateOrderId());
        Integer newId = orderRepository.save(order);

        return "redirect:/reg/atconorder/" + newId + "/attendee/new";
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

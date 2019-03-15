package org.kumoricon.registration.reg;


import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;

import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.tillsession.TillSession;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;


@Controller
public class PaymentController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TillSessionService tillSessionService;
    private final String[] PAYMENT_TYPES = {"cash", "credit", "check"};

    @Autowired
    public PaymentController(OrderRepository orderRepository, UserRepository userRepository, TillSessionService tillSessionService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.tillSessionService = tillSessionService;
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderPayment(Model model,
                             @PathVariable String orderId) {
        Order order = orderRepository.findById(getIdFromParamter(orderId));
        model.addAttribute("order", order);
        return "reg/atcon-order-payment";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment/{paymentType}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderPaymentType(Model model,
                                    @PathVariable String orderId,
                                    @PathVariable String paymentType) {

        if (!isValidPaymentType(paymentType)) {
            throw new RuntimeException("Invalid payment type " + paymentType);
        }
        Order order = orderRepository.findById(getIdFromParamter(orderId));

        Payment p = new Payment();
        p.setOrder(order);
        p.setPaymentType(Payment.PaymentType.valueOf(paymentType.toUpperCase()));

        model.addAttribute("paymentType", paymentType);
        model.addAttribute("payment", p);
        model.addAttribute("order", order);
        return "reg/atcon-order-payment";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment/{paymentType}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderTakePayment(Model model,
                                        @ModelAttribute final Payment payment,
                                        final BindingResult bindingResult,
                                        @PathVariable String orderId,
                                        @PathVariable String paymentType,
                                        Authentication auth) {

        Order order = orderRepository.findById(getIdFromParamter(orderId));
        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("paymentType", paymentType);

        if (bindingResult.hasErrors()) {
            return "reg/atcon-order-payment";
        }

        User currentUser = userRepository.findOneByUsernameIgnoreCase(auth.getName());
        TillSession currentTillSession = tillSessionService.getCurrentSessionForUser(currentUser);
        payment.setPaymentTakenBy(currentUser);
        payment.setPaymentTakenAt(Instant.now());
        payment.setTillSession(currentTillSession);

        orderRepository.save(order);

//        if (order.getTotalAmount().equals(order.getTotalPaid())) {
//            return "redirect:/reg/atconorder/" + order.getId() + "/payment?msg=Added+" + payment.getPaymentType();
//        }

        return "redirect:/reg/atconorder/" + order.getId() + "/payment?msg=Added+" + payment.getPaymentType();

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

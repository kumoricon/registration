package org.kumoricon.registration.reg;


import org.kumoricon.registration.model.badge.BadgeRepository;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;

import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;




@Controller
public class PaymentController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final String[] PAYMENT_TYPES = {"cash", "card", "checkormoneyorder"};

    @Autowired
    public PaymentController(OrderRepository orderRepository, UserRepository userRepository, BadgeRepository badgeRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderPayment(Model model,
                             @PathVariable String orderId,
                             @RequestParam(required = false) String err,
                             @RequestParam(required=false) String msg) {
        Order order = orderRepository.getOne(getIdFromParamter(orderId));
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        model.addAttribute("order", order);
        return "reg/atcon-order-payment";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment/{paymentType}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderPaymentType(Model model,
                                    @PathVariable String orderId,
                                    @PathVariable String paymentType,
                                    @RequestParam(required = false) String err,
                                    @RequestParam(required=false) String msg) {

        if (!isValidPaymentType(paymentType)) {
            throw new RuntimeException("Invalid payment type " + paymentType);
        }
        Order order = orderRepository.getOne(getIdFromParamter(orderId));
        model.addAttribute("paymentType", paymentType);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
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
                                        @RequestParam(required = false) String err,
                                        @RequestParam(required=false) String msg) {

        Order order = orderRepository.getOne(getIdFromParamter(orderId));
        model.addAttribute("order", order);
        model.addAttribute("paymentType", paymentType);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);

        if (bindingResult.hasErrors()) {
            return "reg/atcon-order-payment";
        }

        order.addPayment(payment);
        orderRepository.save(order);

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

package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;

import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.order.PaymentRepository;
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
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Controller
public class PaymentController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TillSessionService tillSessionService;
    private final PaymentRepository paymentRepository;
    private final String[] PAYMENT_TYPES = {"cash", "card", "check"};

    @Autowired
    public PaymentController(OrderRepository orderRepository, UserRepository userRepository,
                             PaymentRepository paymentRepository, TillSessionService tillSessionService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.tillSessionService = tillSessionService;
        this.paymentRepository = paymentRepository;
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderPayment(Model model,
                             @PathVariable Integer orderId) {
        Order order = orderRepository.findById(orderId);
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        BigDecimal totalPaid = BigDecimal.ZERO;
        for (Payment p : payments) {
            totalPaid = totalPaid.add(p.getAmount());
        }
        model.addAttribute("order", order);
        model.addAttribute("orderId", orderId);
        model.addAttribute("totalDue", orderRepository.getTotalByOrderId(orderId));
        model.addAttribute("payments", payments);
        model.addAttribute("totalPaid", totalPaid);
        return "reg/atcon-order-payment";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment/{paymentId}")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderPaymentId(Model model,
                                     @PathVariable Integer orderId,
                                      @PathVariable Integer paymentId) {

        model.addAttribute("order", orderRepository.findById(orderId));
        model.addAttribute("payment", paymentRepository.findById(paymentId));
        model.addAttribute("orderId", orderId);
        return "reg/atcon-order-payment-id";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment/{paymentId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String savePayment(@ModelAttribute @Validated final PaymentFormDTO payment,
                           final BindingResult bindingResult,
                           @PathVariable Integer orderId,
                           @RequestParam(required=false , value = "action") String action,
                           final Model model,
                           final Authentication authentication,
                           HttpServletRequest request) {
        if ("Delete".equals(action) && payment.getId() != null) {
            paymentRepository.deleteById(payment.getId());
            return "redirect:/reg/atconorder/" + orderId + "/payment?msg=Deleted%20payment%20" + payment.getId();
        }
        model.addAttribute("payment", payment);
        if (bindingResult.hasErrors()) {
            return "reg/atcon-order-payment-id";
        }

        Payment paymentData;
        if (payment.getId() != null) {
            paymentData = paymentRepository.findById(payment.getId());
        } else {
            throw new RuntimeException("Error: attempting to save payment with null id");
        }

        paymentData.setAmount(payment.getAmount());
        paymentData.setAuthNumber(payment.getAuthNumber());


        try {
            paymentRepository.save(paymentData);
        } catch (Exception ex) {
            bindingResult.addError(new ObjectError("payment", ex.getMessage()));
            return "reg/atcon-order-payment-id";
        }

        return "redirect:/reg/atconorder/" + orderId + "/payment?msg=Saved%20payment%20" + payment.getId();
    }


    @RequestMapping(value = "/reg/atconorder/{orderId}/payment/new")
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderPaymentType(Model model,
                                    @RequestParam(value="type") String paymentType,
                                    @PathVariable Integer orderId) {

        if (!isValidPaymentType(paymentType)) {
            throw new RuntimeException("Invalid payment type " + paymentType);
        }
        Order order = orderRepository.findById(orderId);
        if (order == null) { throw new RuntimeException("Order " + orderId + " not found"); }

        PaymentFormDTO p = new PaymentFormDTO();
        p.setPaymentType(paymentType);

        model.addAttribute("paymentType", paymentType);
        model.addAttribute("payment", p);
        model.addAttribute("order", order);
        model.addAttribute("payments", paymentRepository.findByOrderId(orderId));
        model.addAttribute("totalPaid", paymentRepository.getTotalPaidForOrder(orderId));
        model.addAttribute("totalDue", orderRepository.getTotalByOrderId(orderId));
        model.addAttribute("order", order);
        return "reg/atcon-order-payment";
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/payment/new", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String atConOrderTakePayment(Model model,
                                        @ModelAttribute @Validated final PaymentFormDTO payment,
                                        final BindingResult bindingResult,
                                        @PathVariable Integer orderId,
                                        Authentication auth,
                                        HttpServletRequest request) {

        Order order = orderRepository.findById(orderId);
        BigDecimal totalDue = orderRepository.getTotalByOrderId(orderId);
        BigDecimal paidSoFar = paymentRepository.getTotalPaidForOrder(orderId);
        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("paymentType", payment.getPaymentType());
        model.addAttribute("payments", paymentRepository.findByOrderId(orderId));
        model.addAttribute("totalPaid", paidSoFar);
        model.addAttribute("totalDue", totalDue);
        model.addAttribute("order", order);

        if (bindingResult.hasErrors()) {
            return "reg/atcon-order-payment";
        }

        User currentUser = userRepository.findOneByUsernameIgnoreCase(auth.getName());
        TillSession currentTillSession = tillSessionService.getCurrentSessionForUser(currentUser);
        Payment paymentData = new Payment();

        paymentData.setOrderId(orderId);
        paymentData.setPaymentTakenBy(currentUser.getId());
        paymentData.setPaymentTakenAt(Instant.now());
        paymentData.setPaymentLocation(request.getRemoteAddr());
        paymentData.setTillSessionId(currentTillSession.getId());
        paymentData.setAmount(payment.getAmount());
        paymentData.setAuthNumber(payment.getAuthNumber());

        switch (payment.getPaymentType()) {
            case "cash":
                paymentData.setPaymentType(Payment.PaymentType.CASH);
                // For cash, don't record a payment of more than the amount due in the order. The user should
                // be giving change. However, we should NOT be giving change for card or check transactions.
                BigDecimal amountLeftToPay = totalDue.subtract(paidSoFar);
                if (payment.getAmount().compareTo(amountLeftToPay) > 0) {
                    paymentData.setAmount(amountLeftToPay);
                }
                break;
            case "card":
                paymentData.setPaymentType(Payment.PaymentType.CREDIT);
                break;
            case "check":
                paymentData.setPaymentType(Payment.PaymentType.CHECK);
                break;
            default:
                throw new RuntimeException("Invalid payment type: " + payment.getPaymentType());
        }

        paymentRepository.save(paymentData);

        if (orderRepository.getTotalByOrderId(orderId).equals(paymentRepository.getTotalPaidForOrder(orderId))) {
            return "redirect:/reg/atconorder/" + order.getId() + "/?msg=Payments%20complete";
        }

        return "redirect:/reg/atconorder/" + order.getId() + "/payment?msg=Added+" + payment.getPaymentType();

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

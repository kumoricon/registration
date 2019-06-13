package org.kumoricon.registration.reg;

import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.controlleradvice.PrinterSettings;
import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.OrderService;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.print.BadgePrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.PrintException;
import java.math.BigDecimal;
import java.util.List;


@Controller
public class AtConRegistrationController {
    private final AttendeeRepository attendeeRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final BadgePrintService badgePrintService;
    private final Logger log = LoggerFactory.getLogger(AtConRegistrationController.class);


    @Autowired
    public AtConRegistrationController(AttendeeRepository attendeeRepository,
                                       OrderRepository orderRepository,
                                       PaymentRepository paymentRepository,
                                       OrderService orderService,
                                       BadgePrintService badgePrintService) {
        this.attendeeRepository = attendeeRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.badgePrintService = badgePrintService;
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
                                @AuthenticationPrincipal User principal,
                                @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        log.info("completing order {} and printing badges", orderId);
        orderService.completeOrder(orderId, principal);
        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);
        List<Attendee> attendees = attendeeRepository.findAllByOrderId(orderId);

        try {
            String result = badgePrintService.printBadgesForAttendees(attendees, printerSettings);
            return "redirect:/reg/atconorder/" + orderId + "/printbadges?msg=" + result;
        } catch (PrintException ex) {
            return "redirect:/reg/atconorder/" + orderId + "/printbadges?err=" + ex.getMessage();
        }
    }

    @RequestMapping(value = "/reg/atconorder/{orderId}/printbadges/reprint/{attendeeId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('at_con_registration')")
    public String reprintBadgeDuringCheckin(@PathVariable Integer orderId,
                                @PathVariable Integer attendeeId,
                                @AuthenticationPrincipal User principal,
                                @CookieValue(value = CookieControllerAdvice.PRINTER_COOKIE_NAME, required = false) String printerCookie) {
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        log.info("reprinting badge during checkin for {}", attendeeId);

        PrinterSettings printerSettings = PrinterSettings.fromCookieValue(printerCookie);

        try {
            String result = badgePrintService.printBadgesForAttendees(List.of(attendee), printerSettings);
            return "redirect:/reg/atconorder/" + orderId + "/printbadges?msg=" + result;
        } catch (PrintException ex) {
            return "redirect:/reg/atconorder/" + orderId + "/printbadges?err=" + ex.getMessage();
        }
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
}

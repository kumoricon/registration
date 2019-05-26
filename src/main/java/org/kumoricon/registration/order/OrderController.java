package org.kumoricon.registration.order;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeHistoryDTO;
import org.kumoricon.registration.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {
    private final OrderRepository orderRepository;
    private final AttendeeRepository attendeeRepository;
    private final PaymentRepository paymentRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final BadgeService badgeService;

    @Autowired
    public OrderController(OrderRepository orderRepository,
                           AttendeeRepository attendeeRepository,
                           PaymentRepository paymentRepository,
                           AttendeeHistoryRepository attendeeHistoryRepository,
                           BadgeService badgeService) {
        this.orderRepository = orderRepository;
        this.attendeeRepository = attendeeRepository;
        this.paymentRepository = paymentRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.badgeService = badgeService;
    }

    @RequestMapping(value = "/orders")
    @PreAuthorize("hasAuthority('manage_orders')")
    public String listOrders(Model model, @RequestParam(required = false) Integer page) {
        Integer nextPage, prevPage;
        if (page == null) { page = 0; }
        if (page > 0) {
            prevPage = page-1;
        } else {
            prevPage = null;
        }
        List<Order> orders = orderRepository.findAllBy(page);

        if (orders.size() == 20) {
            nextPage = page + 1;
        } else {
            nextPage = null;
        }

        model.addAttribute("orders", orders);
        model.addAttribute("totalCount", orderRepository.count());
        model.addAttribute("page", page);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("prevPage", prevPage);
        return "order/orders";
    }

    @RequestMapping(value = "/orders/{orderId}")
    @PreAuthorize("hasAuthority('manage_orders')")
    public String viewOrder(Model model,
                             @PathVariable Integer orderId) {
        return "order/orders-id";
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}")
    @PreAuthorize("hasAuthority('manage_orders')")
    public String viewAttendee(Model model,
                               @PathVariable Integer orderId,
                               @PathVariable Integer attendeeId) {
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        List<AttendeeHistoryDTO> notes = attendeeHistoryRepository.findAllDTObyAttendeeId(attendeeId);

        model.addAttribute("attendee", attendee);
        model.addAttribute("notes", notes);
        model.addAttribute("badgelist", badgeService.findAll());

        return "order/orders-id-attendees-id";
    }

    @RequestMapping(value = "/orders/{orderId}/payments/{paymentId}")
    @PreAuthorize("hasAuthority('manage_orders')")
    public String viewPayment(Model model,
                               @PathVariable Integer orderId,
                               @PathVariable Integer paymentId) {
        return "order/orders-id-payments-id";
    }
}

package org.kumoricon.registration.order;

import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.kumoricon.registration.model.order.OrderDTO;
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
    private final AttendeeDetailRepository attendeeDetailRepository;
    private final PaymentRepository paymentRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final BadgeService badgeService;
    private final AttendeeSearchRepository attendeeSearchRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository,
                           AttendeeSearchRepository attendeeSearchRepository,
                           AttendeeDetailRepository attendeeDetailRepository,
                           PaymentRepository paymentRepository,
                           AttendeeHistoryRepository attendeeHistoryRepository,
                           BadgeService badgeService) {
        this.attendeeSearchRepository = attendeeSearchRepository;
        this.orderRepository = orderRepository;
        this.attendeeDetailRepository = attendeeDetailRepository;
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
        List<OrderDTO> orders = orderRepository.findAllDTOBy(page);

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
    public String listOrdersById(Model model,
                                 @PathVariable Integer orderId) {
        OrderDTO order = orderRepository.findDTOById(orderId);
        model.addAttribute("order", order);
        model.addAttribute("attendees", attendeeSearchRepository.findAllByOrderId(orderId));
        model.addAttribute("payments", paymentRepository.findDTOByOrderId(orderId));
        return "order/orders-id";
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}")
    @PreAuthorize("hasAuthority('attendee_search')")
    public String viewAttendee(Model model,
                               @PathVariable Integer orderId,
                               @PathVariable Integer attendeeId,
                               @RequestParam (required = false) String query,
                               @RequestParam (required = false) String badgeName) {
        AttendeeDetailDTO attendee = attendeeDetailRepository.findByIdAndOrderId(attendeeId, orderId);
        List<AttendeeHistoryDTO> notes = attendeeHistoryRepository.findAllDTObyAttendeeId(attendeeId);

        model.addAttribute("attendee", attendee);
        model.addAttribute("notes", notes);
        model.addAttribute("query", query);
        model.addAttribute("badgeName", badgeName);
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

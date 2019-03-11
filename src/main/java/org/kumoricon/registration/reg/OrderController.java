package org.kumoricon.registration.reg;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.AttendeeSearchRepository;
import org.kumoricon.registration.model.order.Order;
import org.kumoricon.registration.model.order.OrderRepository;
import org.kumoricon.registration.model.order.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    private final OrderRepository orderRepository;
    private final AttendeeRepository attendeeRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository, AttendeeRepository attendeeRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.attendeeRepository = attendeeRepository;
        this.paymentRepository = paymentRepository;
    }

    @RequestMapping(value = "/orders")
    @PreAuthorize("hasAuthority('manage_orders')")
    public String listOrders(Model model,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) String err,
                                     @RequestParam(required=false) String msg) {
        Integer nextPage, prevPage;
        if (page == null) { page = 0; }
        if (page > 0) {
            prevPage = page-1;
        } else {
            prevPage = null;
        }
        Pageable pageable = PageRequest.of(page, 20);
        List<Order> orders = orderRepository.findAllBy(pageable);

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
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "reg/orders";
    }
}

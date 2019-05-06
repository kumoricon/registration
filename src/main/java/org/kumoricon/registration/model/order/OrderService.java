package org.kumoricon.registration.model.order;

import org.kumoricon.registration.model.attendee.AttendeeService;
import org.kumoricon.registration.model.user.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class OrderService {
    private final OrderRepository orderRepository;
    private final AttendeeService attendeeService;

    public OrderService(OrderRepository orderRepository, AttendeeService attendeeService) {
        this.orderRepository = orderRepository;
        this.attendeeService = attendeeService;
    }

    @Transactional
    public void completeOrder(Integer orderId, User user) {
        Order order = orderRepository.findById(orderId);
        if (order.getPaid()) {
            throw new RuntimeException("Order already paid!");
        }

        order.setPaid(true);
        orderRepository.save(order);

        attendeeService.checkInAttendeesInOrder(orderId, user);
    }
}

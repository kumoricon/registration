package org.kumoricon.registration.model.order;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeService;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class OrderService {
    private final OrderRepository orderRepository;
    private final AttendeeService attendeeService;
    private final UserService userService;
    private final BadgeService badgeService;

    public OrderService(OrderRepository orderRepository,
                        AttendeeService attendeeService,
                        UserService userService,
                        BadgeService badgeService) {
        this.orderRepository = orderRepository;
        this.attendeeService = attendeeService;
        this.userService = userService;
        this.badgeService = badgeService;
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

    @Transactional
    public void addAttendeeToOrder(Integer orderId, Attendee attendee, User principal) {
        Order order = orderRepository.findById(orderId);
        order.addToTotalAmount(attendee.getPaidAmount());
        orderRepository.save(order);
        attendee.setOrder(order);
        if (attendee.getBadgeNumber() == null) {
            String badgeNumber = userService.getNextBadgeNumber(principal.getUsername());
            attendee.setBadgeNumber(badgeNumber);
        }
        attendee.setPaidAmount(badgeService.getCostForBadgeType(attendee.getBadgeId(), attendee.getAge()));

        attendeeService.save(attendee);
    }
}

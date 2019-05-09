package org.kumoricon.registration.model.order;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeService;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class OrderService {
    private final OrderRepository orderRepository;
    private final AttendeeService attendeeService;
    private final UserService userService;
    private final BadgeService badgeService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

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
    public void saveAttendeeToOrder(Integer orderId, Attendee attendee, User principal) {
        Order order = orderRepository.findById(orderId);
        checkOrderIsNotPaid(order);

        attendee.setOrder(order);
        if (attendee.getBadgeNumber() == null) {
            String badgeNumber = userService.getNextBadgeNumber(principal.getUsername());
            attendee.setBadgeNumber(badgeNumber);
        }
        if (attendee.getPaidAmount() != null && principal.hasRight("attendee_override_price")) {
            log.info("setting manual price of ${} for {}", attendee.getPaidAmount(), attendee);
        } else {
            attendee.setPaidAmount(badgeService.getCostForBadgeType(attendee.getBadgeId(), attendee.getAge()));
        }

        attendeeService.save(attendee);

        // Recalculate order total
        order.setTotalAmount(orderRepository.getTotalByOrderId(orderId));
        orderRepository.save(order);
    }


    private void checkOrderIsNotPaid(Order order) {
        if (order.getPaid()) throw new RuntimeException("Order " + order.getId() + " is already paid and can not be changed");
    }
}

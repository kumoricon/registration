package org.kumoricon.registration.order;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.BadgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OrderAttendeeEditController {
    private final AttendeeRepository attendeeRepository;
    private final BadgeService badgeService;
    private static final Logger log = LoggerFactory.getLogger(OrderAttendeeEditController.class);

    @Autowired
    public OrderAttendeeEditController(AttendeeRepository attendeeRepository,
                                       BadgeService badgeService) {
        this.attendeeRepository = attendeeRepository;
        this.badgeService = badgeService;
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/edit")
    @PreAuthorize("hasAuthority('attendee_edit')")
    public String editAttendee(Model model,
                               @PathVariable Integer orderId,
                               @PathVariable Integer attendeeId) {
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);

        model.addAttribute("attendee", attendee);
        model.addAttribute("badgelist", badgeService.findAll());

        return "order/orders-id-attendees-id-edit";
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/edit", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('attendee_edit')")
    public String editAttendeeSave(Model model,
                                   @ModelAttribute @Validated final Attendee attendee,
                                   @PathVariable Integer orderId,
                                   @PathVariable Integer attendeeId) {
        if (attendee.getPaidAmount() == null) {
            attendee.setPaidAmount(badgeService.getCostForBadgeType(attendee.getBadgeId(), attendee.getAge()));
        } else {
            log.info("Setting manual price for {} to {}", attendee, attendee.getPaidAmount());
        }

        try {
            attendeeRepository.save(attendee);
            return "redirect:/orders/" + orderId + "/attendees/" + attendeeId + "?msg=Saved";
        } catch (Exception ex) {
            model.addAttribute("attendee", attendee);
            model.addAttribute("badgelist", badgeService.findAll());
            model.addAttribute("err", ex.getMessage());
            return "order/orders-id-attendees-id-edit";
        }
    }
}

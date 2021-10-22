package org.kumoricon.registration.order;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.attendee.AttendeeService;
import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OrderAttendeeAccessibilityStickerController {
    private static final Logger log = LoggerFactory.getLogger(OrderAttendeeAccessibilityStickerController.class);
    private final AttendeeRepository attendeeRepository;

    public OrderAttendeeAccessibilityStickerController(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/accessibilitysticker", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('accessibility_sticker')")
    public String toggleAccessibilitySticker(@PathVariable Integer orderId,
                                             @PathVariable Integer attendeeId) {

        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        boolean newValue = !attendee.getAccessibilitySticker();
        attendee.setAccessibilitySticker(newValue);
        attendeeRepository.save(attendee);

        if (newValue) {
            log.info("added accessibility sticker to {}", attendee);
            return "redirect:/orders/" + orderId + "/attendees/" + attendeeId + "?msg=Added+accessibility+sticker";
        } else {
            log.info("removed accessibility sticker from {}", attendee);
            return "redirect:/orders/" + orderId + "/attendees/" + attendeeId + "?msg=Removed+accessibility+sticker";
        }

    }
}

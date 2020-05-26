package org.kumoricon.registration.order;

import org.kumoricon.registration.model.attendee.*;
import org.kumoricon.registration.model.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OrderAttendeeRevokeController {
    private final AttendeeService attendeeService;
    private final AttendeeRepository attendeeRepository;

    public OrderAttendeeRevokeController(AttendeeService attendeeService,
                                         AttendeeRepository attendeeRepository) {
        this.attendeeService = attendeeService;
        this.attendeeRepository = attendeeRepository;
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/revoke")
    @PreAuthorize("hasAuthority('attendee_revoke_membership')")
    public String revoke(final Model model,
                         @PathVariable Integer orderId,
                         @PathVariable Integer attendeeId) {

        model.addAttribute("attendee", attendeeRepository.findByIdAndOrderId(attendeeId, orderId));
        return "order/orders-id-attendees-id-revoke";
    }


    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/revoke", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('attendee_revoke_membership')")
    public String saveRevoke(@PathVariable Integer orderId,
                           @PathVariable Integer attendeeId,
                           @AuthenticationPrincipal User principal) {

        attendeeService.revokeMembership(attendeeId, orderId, principal);

        return "redirect:/orders/" + orderId + "/attendees/" + attendeeId;
    }
}

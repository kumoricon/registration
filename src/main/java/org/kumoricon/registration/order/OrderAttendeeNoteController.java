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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderAttendeeNoteController {
    private final AttendeeDetailRepository attendeeDetailRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;

    public OrderAttendeeNoteController(AttendeeDetailRepository attendeeDetailRepository,
                                       AttendeeHistoryRepository attendeeHistoryRepository) {
        this.attendeeDetailRepository = attendeeDetailRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/addnote")
    @PreAuthorize("hasAuthority('attendee_add_note')")
    public String addNote(Model model,
                          @PathVariable Integer orderId,
                          @PathVariable Integer attendeeId) {

        AttendeeDetailDTO attendee = attendeeDetailRepository.findByIdAndOrderId(attendeeId, orderId);
        model.addAttribute("attendee", attendee);

        return "order/orders-id-attendees-id-addnote";
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/addnote", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('attendee_add_note')")
    public String saveNote(@PathVariable Integer orderId,
                           @PathVariable Integer attendeeId,
                           @RequestParam String message,
                           @AuthenticationPrincipal User principal) {

        AttendeeDetailDTO attendee = attendeeDetailRepository.findByIdAndOrderId(attendeeId, orderId);
        if (attendee == null) {
            throw new RuntimeException(String.format("Attendee %s not found in order %s", attendeeId, orderId));
        }

        String cleanedMessage = message.trim();
        if (!message.isEmpty()) {
            AttendeeHistory ah = new AttendeeHistory(principal, attendeeId, cleanedMessage);
            attendeeHistoryRepository.save(ah);

            return "redirect:/orders/" + orderId + "/attendees/" + attendeeId + "?msg=Saved";
        } else {
            return "redirect:/orders/" + orderId + "/attendees/" + attendeeId + "?err=Empty+message+not+saved";
        }
    }
}

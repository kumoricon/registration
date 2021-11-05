package org.kumoricon.registration.order;

import org.kumoricon.registration.model.attendee.Attendee;
import org.kumoricon.registration.model.attendee.AttendeeHistory;
import org.kumoricon.registration.model.attendee.AttendeeHistoryRepository;
import org.kumoricon.registration.model.attendee.AttendeeRepository;
import org.kumoricon.registration.model.badge.BadgeService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderAttendeeEditController {
    private final AttendeeRepository attendeeRepository;
    private final AttendeeHistoryRepository attendeeHistoryRepository;
    private final BadgeService badgeService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(OrderAttendeeEditController.class);

    public OrderAttendeeEditController(AttendeeRepository attendeeRepository,
                                       AttendeeHistoryRepository attendeeHistoryRepository,
                                       BadgeService badgeService,
                                       UserService userService) {
        this.attendeeRepository = attendeeRepository;
        this.attendeeHistoryRepository = attendeeHistoryRepository;
        this.badgeService = badgeService;
        this.userService = userService;
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/edit")
    @PreAuthorize("hasAuthority('attendee_edit') || hasAuthority('attendee_edit_with_override')")
    public String editAttendee(Model model,
                               @PathVariable Integer orderId,
                               @PathVariable Integer attendeeId) {
        Attendee attendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);

        model.addAttribute("attendee", attendee);
        model.addAttribute("badgelist", badgeService.findAll());
        return "order/orders-id-attendees-id-edit";
    }

    @RequestMapping(value = "/orders/{orderId}/attendees/{attendeeId}/edit", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('attendee_edit') || hasAuthority('attendee_edit_with_override')")
    public String editAttendeeSave(Model model,
                                   @ModelAttribute @Validated final Attendee attendee,
                                   @RequestParam(value = "overrideUser", required = false) final String overrideUser,
                                   @RequestParam(value = "overridePassword", required = false) final String overridePassword,
                                   @RequestParam(value="note") final String note,
                                   @AuthenticationPrincipal User principal,
                                   @PathVariable Integer orderId,
                                   @PathVariable Integer attendeeId) {

        Attendee serverAttendee = attendeeRepository.findByIdAndOrderId(attendeeId, orderId);
        setAttendeePaidAmount(attendee);
        serverAttendee.updateFrom(attendee);

        try {
            if (principal.hasRight("attendee_edit")) {
                log.info("saved attendee {}", attendee);
                attendeeRepository.save(serverAttendee);
                attendeeHistoryRepository.save(new AttendeeHistory(principal, attendee.getId(), note));
                return "redirect:/orders/" + orderId + "/attendees/" + attendeeId + "?msg=Saved";
            } else {
                User override = (User) userService.loadUserByUsername(overrideUser);
                if (override == null || !userService.validateOverridePassword(override, overridePassword)) {
                    throw new RuntimeException("Bad override username or password");
                } else if (!override.hasRight("attendee_edit")) {
                    throw new RuntimeException("Override user does not have permission to provide override");
                }
                log.info("saved attendee {} with override by {}", serverAttendee, override.getUsername());
                attendeeRepository.save(serverAttendee);
                attendeeHistoryRepository.save(new AttendeeHistory(principal, attendee.getId(), note + "\n\n(Edited with override from " + override +")"));
                return "redirect:/orders/" + orderId + "/attendees/" + attendeeId + "?msg=Saved+with+override";
            }
        } catch (UsernameNotFoundException ex) {
            model.addAttribute("attendee", attendee);
            model.addAttribute("badgelist", badgeService.findAll());
            model.addAttribute("err", "Bad override username or password");
            model.addAttribute("note", note);
            return "order/orders-id-attendees-id-edit";
        } catch (Exception ex) {
            model.addAttribute("attendee", attendee);
            model.addAttribute("badgelist", badgeService.findAll());
            model.addAttribute("err", ex.getMessage());
            model.addAttribute("note", note);
            return "order/orders-id-attendees-id-edit";
        }
    }

    private void setAttendeePaidAmount(Attendee attendee) {
        if (attendee.getPaidAmount() == null) {
            attendee.setPaidAmount(badgeService.getCostForBadgeType(attendee.getBadgeId(), attendee.getAge()));
        } else {
            log.info("Setting manual price for {} to {}", attendee, attendee.getPaidAmount());
        }
    }
}

package org.kumoricon.registration.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GuestController {
    private final GuestRepository guestRepository;

    @Autowired
    public GuestController(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @RequestMapping(value = "/guests")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public String listGuests(Model model, @RequestParam(required = false) Integer page) {
        Integer nextPage, prevPage;
        if (page == null) { page = 0; }
        if (page > 0) {
            prevPage = page-1;
        } else {
            prevPage = null;
        }
        List<Guest> guests = guestRepository.findAllBy(page);

        if (guests.size() == 20) {
            nextPage = page + 1;
        } else {
            nextPage = null;
        }

        model.addAttribute("guests", guests);
        model.addAttribute("totalCount", guestRepository.count());
        model.addAttribute("page", page);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("prevPage", prevPage);
        return "guest/guests";
    }

    @RequestMapping(value = "/guests/{guestId}")
    @PreAuthorize("hasAuthority('print_guest_badge')")
    public String viewGuest(Model model,
                            @PathVariable Integer guestId,
                            @RequestParam(required = false) Integer page) {
        Guest guest = guestRepository.findById(guestId);

        model.addAttribute("guest", guest);
        model.addAttribute("page", page);
        return "guest/guests-id";
    }

}

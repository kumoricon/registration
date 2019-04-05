package org.kumoricon.registration.utility;


import org.kumoricon.registration.model.tillsession.TillSessionDTO;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class TillSessionController {
    private final TillSessionService tillSessionService;
    private final UserService userService;

    public TillSessionController(TillSessionService tillSessionService, UserService userService) {
        this.tillSessionService = tillSessionService;
        this.userService = userService;
    }

    @RequestMapping(value = "/utility/till")
    public String getTillSession(Model model, Principal user) {
        User currentUser = userService.findByUsername(user.getName());
        if (currentUser == null) throw new RuntimeException("User not found");

        TillSessionDTO s = tillSessionService.getOpenSessionForUser(currentUser);

        model.addAttribute("tillSession", s);
        return "utility/tillsession";
    }

    @RequestMapping(value = "/utility/till", method = RequestMethod.POST)
    public String closeTill(Model model, Principal user) {
        User currentUser = userService.findByUsername(user.getName());
        if (currentUser == null) throw new RuntimeException("User not found");

        tillSessionService.closeSessionForUser(currentUser);

        return "redirect:/utility/till?msg=Till%20Session%20Closed";
    }
}
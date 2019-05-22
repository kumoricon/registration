package org.kumoricon.registration.utility;


import org.kumoricon.registration.controlleradvice.CookieControllerAdvice;
import org.kumoricon.registration.model.tillsession.TillSessionDTO;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class TillSessionController {
    private final TillSessionService tillSessionService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(TillSessionController.class);

    public TillSessionController(TillSessionService tillSessionService, UserService userService) {
        this.tillSessionService = tillSessionService;
        this.userService = userService;
    }

    @RequestMapping(value = "/utility/till")
    public String getTillSession(Model model,
                                 @CookieValue(value = CookieControllerAdvice.TILL_COOKIE_NAME, required = false) String tillName,
                                 Principal user) {
        User currentUser = userService.findByUsername(user.getName());
        if (currentUser == null) throw new RuntimeException("User not found");



        TillSessionDTO s = tillSessionService.getOpenSessionForUser(currentUser);

        model.addAttribute("tillName", tillName);
        model.addAttribute("tillSession", s);
        return "utility/tillsession";
    }

    @RequestMapping(value = "/utility/till", method = RequestMethod.POST)
    public String closeTill(Model model,
                            Principal user,
                            @RequestParam String action,
                            @RequestParam String tillName,
                            HttpServletResponse response) {

        User currentUser = userService.findByUsername(user.getName());
        if (currentUser == null) throw new RuntimeException("User not found");

        String sanitizedTillName = tillName.trim();

        if (action.equals("Save")) {
            log.info("Setting till to {}", tillName);
            try {
                Cookie cookie = new Cookie(CookieControllerAdvice.TILL_COOKIE_NAME, sanitizedTillName);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
                return "redirect:/utility/till?msg=Set+till+" + sanitizedTillName;

            } catch (Exception e) {
                model.addAttribute("tillName", tillName);
                model.addAttribute("tillSession", tillSessionService.getOpenSessionForUser(currentUser));

                log.error("Error setting printer to {}", sanitizedTillName, e);
                model.addAttribute("err", e.getMessage());
                return "utility/tillsession";
            }
        } else if (action.equals("Close")) {
            tillSessionService.closeSessionForUser(currentUser);

            return "redirect:/utility/till?msg=Till%20Session%20Closed";
        }

        throw new RuntimeException("Action not specified");
    }
}
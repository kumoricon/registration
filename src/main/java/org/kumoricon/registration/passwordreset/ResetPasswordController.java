package org.kumoricon.registration.passwordreset;

import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ResetPasswordController {
    private static final Logger log = LoggerFactory.getLogger(ResetPasswordController.class);

    private final UserService userService;

    public ResetPasswordController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping(value = "/resetpassword")
    @PreAuthorize("isAuthenticated()")
    public String passwordForm(@AuthenticationPrincipal User user) {
        log.info("{} viewing password reset screen", user);
        return "resetpassword/resetpassword";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    public String savePassword(final Model model,
                               @AuthenticationPrincipal User principal,
                               @RequestParam("password1") String password1,
                               @RequestParam("password2") String password2) {
        if (password1 == null || password1.trim().equals("")) {
            log.warn("tried to set an empty password");
            model.addAttribute("error", "Password may not be empty");
            return "resetpassword/resetpassword";
        }
        if (password1.length() < 4) {
            model.addAttribute("error", "Password must be at least 4 characters");
            log.warn("tried to set a passworld less than 4 characters long");
            return "resetpassword/resetpassword";
        }

        if (!password1.equals(password2)) {
            model.addAttribute("error", "Password does not match");
            return "resetpassword/resetpassword";
        }

        log.info("set a new password.");
        userService.setPassword(principal.getId(), password1);

        Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();

        UserDetails newUser = userService.loadUserByUsername(principal.getUsername());
        AbstractAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newUser, null, newUser.getAuthorities());
        newAuth.setDetails(oldAuth.getDetails());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/";
    }



}
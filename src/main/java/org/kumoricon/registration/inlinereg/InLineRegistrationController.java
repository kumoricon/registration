package org.kumoricon.registration.inlinereg;

import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class InLineRegistrationController {

    private final Logger log = LoggerFactory.getLogger(InLineRegistrationController.class);
    private final InLineRegistrationService inLineRegistrationService;

    @Autowired
    public InLineRegistrationController(InLineRegistrationService inLineRegistrationService) {
        this.inLineRegistrationService = inLineRegistrationService;
    }

    @RequestMapping(value = "/inlinereg/search", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('in_line_registration')")
    public String inLineCheckIn(Model model,
                                @RequestParam(value = "name", required = false) String name) {
        if (name == null) {
            log.info("viewed in-line registration search page");
        } else {
            log.info("searched in-line registration for {}", name);
            model.addAttribute("name", name);
            model.addAttribute("results", inLineRegistrationService.findMatchingByName(name));
        }
        return "inlinereg/search";
    }

    @RequestMapping(value = "/inlinereg/checkin", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('in_line_registration')")
    public String inLineCheckInPost(Model model,
                                    @RequestParam String code,
                                    @AuthenticationPrincipal User user) {
        log.info("Creating order for in-line registration code {}", code);

        try {
            int orderId = inLineRegistrationService.createOrder(code, user);
            return "redirect:/reg/atconorder/" + orderId;
        } catch (InLineRegistrationException ex) {
            log.warn("Error creating order: {}", ex.getMessage());
            model.addAttribute("err", "No attendees found for registration code " + code);
            return "inlinereg/search";
        }
    }
}

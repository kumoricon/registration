package org.kumoricon.registration.admin.tillsession;

import org.kumoricon.registration.model.tillsession.TillSessionDTO;
import org.kumoricon.registration.model.tillsession.TillSessionService;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class TillSessionAdminController {
    private final TillSessionService tillSessionService;
    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(TillSessionAdminController.class);

    @Autowired
    public TillSessionAdminController(TillSessionService tillSessionService, UserService userService) {
        this.tillSessionService = tillSessionService;
        this.userService = userService;
    }

    @RequestMapping(value = "/admin/tillsessions")
    @PreAuthorize("hasAuthority('manage_till_sessions')")
    public String admin(Model model,
                        @RequestParam(value = "filter", defaultValue = "open", required = false) String filter) {

        model.addAttribute("tillSessions", getTillSessions(filter));
        model.addAttribute("filter", filter);
        return "admin/tillsessions";
    }


    @RequestMapping(value = "/admin/tillsessions/{id}")
    @PreAuthorize("hasAuthority('manage_till_sessions')")
    public String viewTillSessionReport(Model model,
                                        @PathVariable Integer id,
                                        @RequestParam(required = false, defaultValue = "false") Boolean showIndividualOrders) {
        model.addAttribute("tillSession", tillSessionService.getTillDetailDTO(id));
        model.addAttribute("showIndividualOrders", showIndividualOrders);

        return "reports/tillsessions-id";
    }


    @RequestMapping(value = "/admin/tillsessions", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('manage_till_sessions')")
    public String closeSession(Model model,
                               @RequestParam Integer sessionId,
                               @RequestParam(value="filter", defaultValue = "open", required = false) String filter,
                               Principal principal) {

        User user = userService.findByUsername(principal.getName());
        log.info("{} closing till session {}", user, sessionId);

        tillSessionService.closeSession(sessionId);

        model.addAttribute("msg", "Session " + sessionId + " closed");
        model.addAttribute("filter", filter);
        model.addAttribute("tillSessions", getTillSessions(filter));
        return "admin/tillsessions";
    }


    private List<TillSessionDTO> getTillSessions(String filter) {
        if ("open".equals(filter)) {
            return tillSessionService.getOpenTillSessionDTOs();
        } else {
            return tillSessionService.getAllTillSessionDTOs();
        }
    }

}

package org.kumoricon.registration.admin.badges;

import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BadgeController {
    private final BadgeService badgeService;

    private static final Logger log = LoggerFactory.getLogger(BadgeController.class);

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }


    @RequestMapping(value = "/admin/badgetypes")
    @PreAuthorize("hasAuthority('manage_pass_types')")
    public String listBadgeTypes(Model model) {
        List<Badge> badges = badgeService.findAll();

        model.addAttribute("badges", badges);
        return "admin/badgetypes";
    }

    @RequestMapping(value = "/admin/badgetypes", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('manage_pass_types')")
    public String toggleBadgeType(@RequestParam(value="id") Integer id,
                                  @RequestParam(value = "action") String action) {
        if ("Show".equals(action)) {
            log.info("Showing badge {}", id);
            badgeService.setBadgeVisibility(id, true);
            return "redirect:/admin/badgetypes?msg=Badge%20" + id + "%20shown";
        } else if ("Hide".equals(action)){
            log.info("Hiding badge {}", id);
            badgeService.setBadgeVisibility(id, false);
            return "redirect:/admin/badgetypes?msg=Badge%20" + id + "%20hidden";
        } else {
            throw new RuntimeException("Bad action: " + action);
        }
    }
}

package org.kumoricon.registration.admin.blacklist;

import org.kumoricon.registration.model.blacklist.BlacklistName;
import org.kumoricon.registration.model.blacklist.BlacklistRepository;
import org.kumoricon.registration.model.blacklist.BlacklistValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BlacklistController {
    private final BlacklistRepository blacklistRepository;
    private final BlacklistValidator blacklistValidator;
    private static final Logger log = LoggerFactory.getLogger(BlacklistController.class);

    @Autowired
    public BlacklistController(BlacklistRepository blacklistRepository, BlacklistValidator blacklistValidator) {
        this.blacklistRepository = blacklistRepository;
        this.blacklistValidator = blacklistValidator;
    }

    @InitBinder("blacklistName")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(blacklistValidator);
    }

    @RequestMapping(value = "/admin/blacklist")
    @PreAuthorize("hasAuthority('manage_blacklist')")
    public String listBlacklistNames(Model model) {
        List<BlacklistName> names = blacklistRepository.findAll();

        model.addAttribute("names", names);
        return "admin/blacklist";
    }

    @RequestMapping(value = "/admin/blacklist/{id}")
    @PreAuthorize("hasAuthority('manage_blacklist')")
    public String editBlacklistName(@PathVariable String id, final Model model) {
        BlacklistName blacklistName;

        if (id.toLowerCase().equals("new")) {
            blacklistName = new BlacklistName();
        } else {
            blacklistName = blacklistRepository.findById(Integer.parseInt(id));
        }

        model.addAttribute("blacklistName", blacklistName);

        return "admin/blacklist-id";
    }


    @RequestMapping("/admin/blacklist/save")
    @PreAuthorize("hasAuthority('manage_blacklist')")
    public String saveUser(@ModelAttribute @Validated final BlacklistName blacklistName,
                           @RequestParam(required=false , value = "action") String action,
                           final BindingResult bindingResult) {
        if ("Delete".equals(action)) {
            log.info("deleted blacklist entry {}", blacklistName);
            blacklistRepository.delete(blacklistName);
            return "redirect:/admin/blacklist?msg=Deleted%20" + blacklistName.getFirstName() + "%20" + blacklistName.getLastName();
        }

        if (bindingResult.hasErrors()) {
            return "admin/blacklist-id";
        }

        try {
            log.info("saved blacklist entry {}", blacklistName);
            blacklistRepository.save(blacklistName);
        } catch (Exception ex) {
            bindingResult.addError(new ObjectError("blacklistName", ex.getMessage()));
            return "admin/blacklist-id";
        }

        return "redirect:/admin/blacklist?msg=Saved%20" + blacklistName.getFirstName() + "%20" + blacklistName.getLastName();
    }
}

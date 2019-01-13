package org.kumoricon.registration.admin.blacklist;

import org.kumoricon.registration.model.blacklist.BlacklistName;
import org.kumoricon.registration.model.blacklist.BlacklistRepository;
import org.kumoricon.registration.model.blacklist.BlacklistValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class BlacklistController {
    private final BlacklistRepository blacklistRepository;
    private final BlacklistValidator blacklistValidator;

    @Autowired
    public BlacklistController(BlacklistRepository blacklistRepository, BlacklistValidator blacklistValidator) {
        this.blacklistRepository = blacklistRepository;
        this.blacklistValidator = blacklistValidator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(blacklistValidator);
    }

    @RequestMapping(value = "/admin/blacklist")
    @PreAuthorize("hasAuthority('manage_blacklist')")
    public String listBlacklistNames(Model model, @RequestParam(required = false) String err, @RequestParam(required=false) String msg) {
        List<BlacklistName> names = blacklistRepository.findAll();

        model.addAttribute("names", names);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "admin/blacklist";
    }

    @RequestMapping(value = "/admin/blacklist/{id}")
    @PreAuthorize("hasAuthority('manage_blacklist')")
    public String editBlacklistName(@PathVariable String id, final Model model) {
        BlacklistName blacklistName;

        if (id.toLowerCase().equals("new")) {
            blacklistName = new BlacklistName();
        } else {
            Optional<BlacklistName> name = blacklistRepository.findById(Integer.parseInt(id));
            if (name.isPresent()) {
                blacklistName = name.get();
            } else {
                throw new RuntimeException("Blacklist entry " + id + " not found");
            }
        }

        model.addAttribute("blacklistName", blacklistName);

        return "admin/blacklist-id";
    }


    @RequestMapping("/admin/blacklist/save")
    @PreAuthorize("hasAuthority('manage_blacklist')")
    public String saveUser(@ModelAttribute @Validated final BlacklistName blacklistName,
                           @RequestParam(required=false , value = "action") String action,
                           final BindingResult bindingResult,
                           final Model model,
                           HttpServletRequest request) {
        if ("Delete".equals(action)) {
            blacklistRepository.delete(blacklistName);
            return "redirect:/admin/blacklist?msg=Deleted%20" + blacklistName.getFirstName() + "%20" + blacklistName.getLastName();
        }


        if (bindingResult.hasErrors()) {
            return "admin/blacklist-id";
        }

        try {
            blacklistRepository.save(blacklistName);
        } catch (Exception ex) {
            bindingResult.addError(new ObjectError("blacklistName", ex.getMessage()));
            return "admin/blacklist-id";
        }

        return "redirect:/admin/blacklist?msg=Saved%20" + blacklistName.getFirstName() + "%20" + blacklistName.getLastName();
    }
}

package org.kumoricon.registration.admin.user;

import org.kumoricon.registration.model.role.RoleRepository;
import org.kumoricon.registration.model.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserValidator userValidator;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, RoleRepository roleRepository, UserService userService, UserValidator userValidator) {
        this.userRepository = userRepository;   // TODO: Refactor this so it only depends on UserService, not
        this.roleRepository = roleRepository;   // Repositories directly.
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @InitBinder("user")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
    }

    /**
     * Display the user list. "msg" and "err" are query parameters, used to display an errors or messages such as
     * "saved [username]". Those are set during the redirect on whatever page sends the browser here.
     */
    @RequestMapping(value = "/admin/users")
    @PreAuthorize("hasAuthority('manage_users')")
    public String listUsers(final Model model) {
        log.info("viewing user list");
        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * Display a given user from the database, or a blank form if the user id from the URL is "new"
     * @param userId User ID or "new"
     * @param model Spring MVC response model
     */
    @RequestMapping(value = "/admin/users/{userId}")
    @PreAuthorize("hasAuthority('manage_users')")
    public String editUser(@PathVariable String userId,
                           final Model model) {
        log.info("viewing user id {}", userId);
        User user;
        if (userId.equalsIgnoreCase("new")) {
            user = userService.newUser();
        } else {
            try {
                user = userService.findById(Integer.parseInt(userId));
            } catch (UserIdNotFoundException ex) {
                user = null;
                log.error("User ID {} not found", userId);
                model.addAttribute("err", "Error: User " + userId + " not found");
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());

        return "admin/users-userid";
    }


    /**
     * Handle saving updated user, and possibly resetting password. On success, will redirect to the user list,
     * and on failure will show whatever errors have been raised by the validator, and/or any exceptions from
     * saving to the database.
     */
    @RequestMapping("/admin/users/save")
    @PreAuthorize("hasAuthority('manage_users')")
    public String saveUser(@ModelAttribute @Validated final User user,
                           @RequestParam(required=false , value = "action") String action,
                           final BindingResult bindingResult,
                           final Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/users-userid";
        }

        log.info("saving user {}", user);

        try {
            userService.updateUser(user);
        } catch (Exception ex) {
            bindingResult.addError(new ObjectError("User", ex.getMessage()));
            model.addAttribute("roles", roleRepository.findAll());
            log.error("error saving {}", user, ex);
            return "admin/users-userid";
        }

        if ("Reset Password".equals(action)) {
            userService.resetPassword(user.getId());
            log.info("reset password for user {}. Current sessions will be logged out", user);
            return "redirect:/admin/users?msg=Reset%20password%20for%20" + user.getUsername() + ".%20They%20will%20be%20logged%20out";
        }

        return "redirect:/admin/users?msg=Saved%20" + user.getUsername();
    }
}

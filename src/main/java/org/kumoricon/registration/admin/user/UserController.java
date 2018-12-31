package org.kumoricon.registration.admin.user;

import org.kumoricon.registration.model.role.RoleRepository;
import org.kumoricon.registration.model.user.*;
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

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository, UserService userService, UserValidator userValidator) {
        this.userRepository = userRepository;   // TODO: Refactor this so it only depends on UserService, not
        this.roleRepository = roleRepository;   // Repositories directly.
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
    }

    /**
     * Display the user list. "msg" and "err" are query parameters, used to display an errors or messages such as
     * "saved [username]". Those are set during the redirect on whatever page sends the browser here.
     */
    @RequestMapping(value = "/admin/users")
    @PreAuthorize("hasAuthority('manage_users')")
    public String listUsers(Model model, @RequestParam(required = false) String err, @RequestParam(required=false) String msg) {
        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "admin/users";
    }

    /**
     * Display a given user from the database, or a blank for if the user id from the URL is "new"
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/users/{userId}")
    @PreAuthorize("hasAuthority('manage_users')")
    public String editUser(@PathVariable String userId, final Model model) {
        User user;
        if (userId.toLowerCase().equals("new")) {
            user = userService.newUser();
        } else {
            try {
                user = userService.findById(Integer.parseInt(userId));
            } catch (UserIdNotFoundException ex) {
                user = null;
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
     * saving to the database. Right now the execeptions from the database are kind of useless, it would be nice
     * to say "couldn't save because of duplicate badge prefix 'AU'" instead of the current "save failed due to
     * database constraint FK_1234SDKFJHSDKFAFDSAF" kind of message.
     */
    @RequestMapping("/admin/users/save")
    @PreAuthorize("hasAuthority('manage_users')")
    public String saveUser(@ModelAttribute @Validated final User user,
                           @RequestParam(required=false , value = "action") String action,
                           final BindingResult bindingResult,
                           HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "admin/users-userid";
        }

        try {
            userService.updateUser(user);
        } catch (Exception ex) {
            bindingResult.addError(new ObjectError("User", ex.getMessage()));
            return "admin/users-userid";
        }

        if ("Reset Password".equals(action)) {
            userService.resetPassword(user.getId());
        }

        return "redirect:/admin/users?msg=Saved%20" + user.getUsername();

    }
}

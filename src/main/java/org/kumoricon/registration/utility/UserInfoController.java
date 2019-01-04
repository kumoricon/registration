package org.kumoricon.registration.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class UserInfoController {

    @Autowired
    public UserInfoController() {
    }

    @RequestMapping(value = "/utility/userinfo")
    public String admin(Model model) {

        return "utility/userinfo";
    }
}

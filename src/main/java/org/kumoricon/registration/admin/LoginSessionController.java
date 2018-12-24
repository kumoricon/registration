package org.kumoricon.registration.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class LoginSessionController {


    @RequestMapping(value = "/admin/loginsessions")
    public String admin(Map<String, Object> model) {

        return "admin/loginsessions";
    }


}

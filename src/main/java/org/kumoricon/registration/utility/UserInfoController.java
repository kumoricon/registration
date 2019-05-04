package org.kumoricon.registration.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
public class UserInfoController {

    @Autowired
    public UserInfoController() {
    }

    @RequestMapping(value = "/utility/userinfo")
    public String admin(Model model, HttpServletRequest request) {
        model.addAttribute("clientIp", getClientIp(request));
        return "utility/userinfo";
    }

    private static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }
}

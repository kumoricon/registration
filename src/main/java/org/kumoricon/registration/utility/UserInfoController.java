package org.kumoricon.registration.utility;

import org.kumoricon.registration.model.role.Right;
import org.kumoricon.registration.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.TreeSet;


@Controller
public class UserInfoController {

    @Autowired
    public UserInfoController() {
    }

    @RequestMapping(value = "/utility/userinfo")
    public String admin(Model model, HttpServletRequest request, @AuthenticationPrincipal User principal) {
        Set<Right> rights = new TreeSet<>(principal.getRights());
        model.addAttribute("clientIp", getClientIp(request));
        model.addAttribute("rights", rights);
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

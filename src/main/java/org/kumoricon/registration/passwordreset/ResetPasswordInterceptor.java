package org.kumoricon.registration.passwordreset;

import org.kumoricon.registration.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class ResetPasswordInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(ResetPasswordInterceptor.class);

    private final String[] IGNORE_PATHS = {"/webjars", "/css/", "/img", "/js", "/favicon.ico", "/resetpassword"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        for (String prefix : IGNORE_PATHS) {
            if (request.getRequestURI().startsWith(prefix)) {
                return true;
            }
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            if (user.getForcePasswordChange()) {
                try {
                    log.info("Redirecting {} to /resetpassword", user);
                    response.sendRedirect("/resetpassword");
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }


}
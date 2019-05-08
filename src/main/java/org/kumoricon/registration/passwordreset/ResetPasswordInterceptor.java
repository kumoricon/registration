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
        // If the request's URI starts with one of the ignored paths (images, css, etc), just continue sending that
        // resource
        String uri = request.getRequestURI();
        for (String prefix : IGNORE_PATHS) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }

        // Get the current user. If their forcePasswordChange property is true, redirect to /resetpassword
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            if (user.getForcePasswordChange()) {
                try {
                    log.info("forcePasswordChange set, redirecting to /resetpassword");
                    response.sendRedirect("/resetpassword");
                    return false;
                } catch (IOException e) {
                    log.error("Error redirecting to /resetpassword", e);
                }
            }
        }

        return true;
    }


}
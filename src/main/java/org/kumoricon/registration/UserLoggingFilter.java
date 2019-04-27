package org.kumoricon.registration;

import org.kumoricon.registration.model.user.User;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * Adds user information to every request, which is included when logging messages
 */
@Component
public class UserLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // Add user to MDC data. This is logged in the logging.pattern.console and logging.pattern.file settings
            // in application.properties
            MDC.put("user", user());
            chain.doFilter(request, response);
        } finally {
            // Tear down MDC data: (Important! Cleans up the ThreadLocal data again)
            MDC.clear();
        }
    }

    /**
     * Get a String representing the user in the current context, or "anonymous" if there isn't one.
     * If available, this includes both the ID from the database and the username, which will make it
     * possible to follow a user if their username is changed.
     * @return String
     */
    private String user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return principal.toString();
        } else if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        } else {
            return "[anonymous]";
        }
    }
}
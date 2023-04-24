package org.kumoricon.registration.admin.roles;

import org.kumoricon.registration.model.role.Right;
import org.kumoricon.registration.model.role.RightRepository;
import org.kumoricon.registration.model.user.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Interceptor to update user rights with saved rights from database. This is to allow user rights to be updated
 * without requiring logging in/out
 */
@Component
public class UserRightsUpdateInterceptor implements HandlerInterceptor {

    private static final Set<Integer> USER_ID_CACHE = new HashSet<>();
    private final RightRepository rightRepository;

    public UserRightsUpdateInterceptor(RightRepository rightRepository) {
        this.rightRepository = rightRepository;
    }

    /**
     * Updates security context with the newest user authorities loaded from database
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<GrantedAuthority> authorities = new HashSet<>();
        Object principal = auth.getPrincipal();

        if (principal instanceof User user && !USER_ID_CACHE.contains(user.getId())) {
            // Load rights from database
            Set<Right> rights = rightRepository.findAllRightsByUserId(user.getId());
            authorities.addAll(rights);

            // Add new authentication with the freshly loaded rights to current security context
            AbstractAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(user, null, authorities);
            newAuth.setDetails(auth.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // Add userId to cache so we don't unnecessarily check database for each request
            USER_ID_CACHE.add(user.getId());
        }

        return true;
    }

    /**
     * Clears the userId cache.
     * This should be called after updating a role's rights so that logged-in user's rights can be updated
     */
    public static void clearCache() {
        USER_ID_CACHE.clear();
    }
}
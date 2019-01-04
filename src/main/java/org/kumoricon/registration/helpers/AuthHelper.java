package org.kumoricon.registration.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthHelper {
    public static boolean userHasAuthority(Authentication authentication, String right) {
        if (right == null || right.trim().isEmpty()) return true;
        if (authentication == null) return false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (right.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}

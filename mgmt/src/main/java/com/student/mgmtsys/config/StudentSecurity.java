package com.student.mgmtsys.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Method-security helper used from {@code @PreAuthorize} to ensure a
 * header-authenticated student can only act on their own resources.
 */
@Component("studentSecurity")
public class StudentSecurity {

    /**
     * @return true when the authenticated student's id equals the id in the request path.
     */
    public boolean isSelf(Long studentId, Authentication authentication) {
        if (authentication == null || studentId == null) {
            return false;
        }
        return authentication.getPrincipal() instanceof StudentPrincipal principal
                && studentId.equals(principal.id());
    }
}

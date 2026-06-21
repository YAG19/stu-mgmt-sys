package com.student.mgmtsys.config;

/**
 * Principal stored in the SecurityContext for a header-authenticated student.
 * Carries the id so endpoints can verify the caller owns the requested resource.
 */
public record StudentPrincipal(Long id, String code) {
}

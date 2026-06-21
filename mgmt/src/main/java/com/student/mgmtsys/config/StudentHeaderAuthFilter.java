package com.student.mgmtsys.config;

import com.student.mgmtsys.repository.StudentRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class StudentHeaderAuthFilter extends OncePerRequestFilter {

    private static final String CODE_HEADER = "X-Student-Code";
    private static final String DOB_HEADER = "X-Student-Dob";

    private final StudentRepository studentRepository;

    public StudentHeaderAuthFilter(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String code = request.getHeader(CODE_HEADER);
        String dob = request.getHeader(DOB_HEADER);


        if (code != null && dob != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
            LocalDate dateOfBirth = LocalDate.parse(dob);
                studentRepository.findByCode(code)
                        .filter(student -> dateOfBirth.equals(student.getDateOfBirth()))
                        .ifPresent(student -> {
                            var principal = new StudentPrincipal(student.getId(), student.getCode());
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    principal, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
                            authentication.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            } catch (DateTimeParseException ex) {
                // malformed date header -> leave unauthenticated; authorization rules return 401/403
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}

package com.student.mgmtsys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final StudentHeaderAuthFilter studentHeaderAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          StudentHeaderAuthFilter studentHeaderAuthFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.studentHeaderAuthFilter = studentHeaderAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // stateless JWT API: no CSRF tokens, no HTTP session
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()            // login is open
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")      // role comes from the JWT
                        .requestMatchers("/api/students/**").hasRole("STUDENT") // role comes from the X-Student-* headers
                        .anyRequest().permitAll())                              // other endpoints open for now

                // no/invalid credentials -> 401 (instead of the default 403 when no entry point is set)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // authenticate via Bearer token (admin) and X-Student-* headers (student)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(studentHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

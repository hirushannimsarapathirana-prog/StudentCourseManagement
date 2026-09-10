package org.example.studentcoursemanagement.config;

import org.example.studentcoursemanagement.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthFilter jwtAuthFilter) {

        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // AUTH
                        // =========================

                        .requestMatchers("/api/auth/**")
                        .permitAll()


                        // =========================
                        // USERS
                        // =========================

                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")


                        // =========================
                        // STUDENTS
                        // =========================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/students"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/students"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/api/students/**"
                        )
                        .hasAnyRole("ADMIN", "STUDENT")


                        // =========================
                        // COURSES
                        // =========================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/courses/**"
                        )
                        .hasAnyRole("ADMIN", "STUDENT")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/courses/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/courses/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/courses/**"
                        )
                        .hasRole("ADMIN")


                        // =========================
                        // ENROLLMENTS
                        // =========================

                        // CREATE
                        // ADMIN + STUDENT
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/enrollments"
                        )
                        .hasAnyRole("ADMIN", "STUDENT")


                        // GET
                        // Controller checks ownership
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/enrollments/**"
                        )
                        .hasAnyRole("ADMIN", "STUDENT")


                        // UPDATE
                        // ADMIN only
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/enrollments/**"
                        )
                        .hasRole("ADMIN")


                        // DELETE
                        // ADMIN only
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/enrollments/**"
                        )
                        .hasRole("ADMIN")


                        // =========================
                        // OTHER
                        // =========================

                        .anyRequest()
                        .authenticated()
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
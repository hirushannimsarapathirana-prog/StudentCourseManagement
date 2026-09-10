package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.LoginRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String login(LoginRequestDTO request) {

        // Create username + password authentication request
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        // Authenticate user
        Authentication authentication =
                authenticationManager.authenticate(
                        authenticationToken
                );

        // Get authenticated user's details
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        // Generate JWT with username + role
        return jwtService.generateToken(userDetails);
    }
}
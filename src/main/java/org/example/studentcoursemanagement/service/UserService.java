package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.UserRequestDTO;
import org.example.studentcoursemanagement.dto.UserResponseDTO;
import org.example.studentcoursemanagement.entity.User;
import org.example.studentcoursemanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserRequestDTO request) {

        User user = new User();

        user.setUsername(request.getUsername());

        // Hash the password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }
}
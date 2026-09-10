package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.UserRequestDTO;
import org.example.studentcoursemanagement.dto.UserResponseDTO;
import org.example.studentcoursemanagement.entity.Student;
import org.example.studentcoursemanagement.entity.User;
import org.example.studentcoursemanagement.repository.StudentRepository;
import org.example.studentcoursemanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            StudentRepository studentRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
    }


    // ==========================================
    // CREATE USER
    // ==========================================

    public UserResponseDTO createUser(
            UserRequestDTO request) {

        // Check duplicate username
        if (userRepository.findByUsername(
                request.getUsername()).isPresent()) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        // Check valid role
        String role = request.getRole().toUpperCase();

        if (!role.equals("ADMIN") &&
                !role.equals("STUDENT")) {

            throw new RuntimeException(
                    "Role must be ADMIN or STUDENT"
            );
        }

        // STUDENT must have student ID
        if (role.equals("STUDENT") &&
                request.getStudentId() == null) {

            throw new RuntimeException(
                    "Student ID is required for STUDENT role"
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());

        // Password hashing
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(role);


        // Link student account
        if (role.equals("STUDENT")) {

            Student student =
                    studentRepository.findById(
                            request.getStudentId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Student not found"
                            )
                    );

            user.setStudent(student);
        }

        User savedUser =
                userRepository.save(user);

        return convertToResponseDTO(savedUser);
    }


    // ==========================================
    // GET ALL USERS
    // ==========================================

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    // ==========================================
    // GET USER BY ID
    // ==========================================

    public Optional<UserResponseDTO> getUserById(
            Long id) {

        return userRepository.findById(id)
                .map(this::convertToResponseDTO);
    }


    // ==========================================
    // UPDATE USER
    // ==========================================

    public UserResponseDTO updateUser(
            Long id,
            UserRequestDTO request) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));


        // Check duplicate username
        Optional<User> existingUser =
                userRepository.findByUsername(
                        request.getUsername()
                );

        if (existingUser.isPresent() &&
                !existingUser.get()
                        .getId()
                        .equals(id)) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }


        String role =
                request.getRole().toUpperCase();

        if (!role.equals("ADMIN") &&
                !role.equals("STUDENT")) {

            throw new RuntimeException(
                    "Role must be ADMIN or STUDENT"
            );
        }


        user.setUsername(
                request.getUsername()
        );

        user.setRole(role);


        // Update password only if provided
        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }


        // Student role → link student
        if (role.equals("STUDENT")) {

            if (request.getStudentId() == null) {

                throw new RuntimeException(
                        "Student ID is required for STUDENT role"
                );
            }

            Student student =
                    studentRepository.findById(
                            request.getStudentId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Student not found"
                            )
                    );

            user.setStudent(student);

        } else {

            // ADMIN does not need a student
            user.setStudent(null);
        }


        User updatedUser =
                userRepository.save(user);

        return convertToResponseDTO(updatedUser);
    }


    // ==========================================
    // DELETE USER
    // ==========================================

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {

            throw new RuntimeException(
                    "User not found"
            );
        }

        userRepository.deleteById(id);
    }


    // ==========================================
    // ENTITY → RESPONSE DTO
    // ==========================================

    private UserResponseDTO convertToResponseDTO(
            User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
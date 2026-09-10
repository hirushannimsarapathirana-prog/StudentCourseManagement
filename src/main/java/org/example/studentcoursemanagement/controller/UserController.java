package org.example.studentcoursemanagement.controller;

import jakarta.validation.Valid;
import org.example.studentcoursemanagement.dto.UserRequestDTO;
import org.example.studentcoursemanagement.dto.UserResponseDTO;
import org.example.studentcoursemanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {

        this.userService = userService;
    }


    // ==========================================
    // CREATE USER
    // ADMIN ONLY
    // ==========================================

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO request) {

        return ResponseEntity.ok(
                userService.createUser(request)
        );
    }


    // ==========================================
    // GET ALL USERS
    // ADMIN ONLY
    // ==========================================

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>>
    getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }


    // ==========================================
    // GET USER BY ID
    // ADMIN ONLY
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO>
    getUserById(
            @PathVariable Long id) {

        return userService
                .getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // ==========================================
    // UPDATE USER
    // ADMIN ONLY
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO>
    updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO request) {

        return ResponseEntity.ok(
                userService.updateUser(
                        id,
                        request
                )
        );
    }


    // ==========================================
    // DELETE USER
    // ADMIN ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
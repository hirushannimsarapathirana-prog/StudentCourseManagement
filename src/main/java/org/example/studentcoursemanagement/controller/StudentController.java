package org.example.studentcoursemanagement.controller;

import jakarta.validation.Valid;
import org.example.studentcoursemanagement.dto.StudentRequestDTO;
import org.example.studentcoursemanagement.dto.StudentResponseDTO;
import org.example.studentcoursemanagement.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Valid @RequestBody StudentRequestDTO request) {

        return ResponseEntity.ok(
                studentService.createStudent(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents(
            Authentication authentication) {

        // ADMIN can view all students
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            return ResponseEntity.ok(
                    studentService.getAllStudents()
            );
        }

        // STUDENT cannot view all students
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwnProfile =
                studentService.isOwnProfile(
                        id,
                        authentication.getName()
                );

        // ADMIN can view any student
        // STUDENT can view only own profile
        if (!isAdmin && !isOwnProfile) {
            return ResponseEntity.status(403).build();
        }

        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO request,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwnProfile =
                studentService.isOwnProfile(
                        id,
                        authentication.getName()
                );

        // ADMIN can update any student
        // STUDENT can update only own profile
        if (!isAdmin && !isOwnProfile) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                studentService.updateStudent(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Only ADMIN can delete students
        if (!isAdmin) {
            return ResponseEntity.status(403).build();
        }

        studentService.deleteStudent(id);

        return ResponseEntity.noContent().build();
    }
}
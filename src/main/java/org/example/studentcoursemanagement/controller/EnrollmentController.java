package org.example.studentcoursemanagement.controller;

import jakarta.validation.Valid;
import org.example.studentcoursemanagement.dto.EnrollmentRequestDTO;
import org.example.studentcoursemanagement.dto.EnrollmentResponseDTO;
import org.example.studentcoursemanagement.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            EnrollmentService enrollmentService) {

        this.enrollmentService = enrollmentService;
    }


    // ==========================================
    // CREATE ENROLLMENT
    // ADMIN + STUDENT
    // ==========================================

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(
            @Valid @RequestBody EnrollmentRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                enrollmentService.createEnrollment(
                        request,
                        authentication.getName()
                )
        );
    }


    // ==========================================
    // GET ALL
    // ADMIN ONLY
    // ==========================================

    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDTO>>
    getAllEnrollments(Authentication authentication) {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        if (!isAdmin) {
            return ResponseEntity
                    .status(403)
                    .build();
        }

        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments()
        );
    }


    // ==========================================
    // GET BY ID
    // ADMIN + OWN STUDENT
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO>
    getEnrollmentById(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        boolean isOwn =
                enrollmentService.isOwnEnrollment(
                        id,
                        authentication.getName()
                );

        if (!isAdmin && !isOwn) {
            return ResponseEntity
                    .status(403)
                    .build();
        }

        return enrollmentService
                .getEnrollmentById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // ==========================================
    // GET BY STUDENT
    // ADMIN + OWN STUDENT
    // ==========================================

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponseDTO>>
    getEnrollmentsByStudent(
            @PathVariable Long studentId,
            Authentication authentication) {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        boolean isOwn =
                enrollmentService.isOwnStudent(
                        studentId,
                        authentication.getName()
                );

        if (!isAdmin && !isOwn) {
            return ResponseEntity
                    .status(403)
                    .build();
        }

        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollmentsByStudent(studentId)
        );
    }


    // ==========================================
    // GET BY COURSE
    // ADMIN ONLY
    // ==========================================

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDTO>>
    getEnrollmentsByCourse(
            @PathVariable Long courseId,
            Authentication authentication) {

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        if (!isAdmin) {
            return ResponseEntity
                    .status(403)
                    .build();
        }

        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollmentsByCourse(courseId)
        );
    }


    // ==========================================
    // UPDATE
    // ADMIN ONLY
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO>
    updateEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentRequestDTO request) {

        return ResponseEntity.ok(
                enrollmentService.updateEnrollment(
                        id,
                        request
                )
        );
    }


    // ==========================================
    // DELETE
    // ADMIN ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Long id) {

        enrollmentService.deleteEnrollment(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
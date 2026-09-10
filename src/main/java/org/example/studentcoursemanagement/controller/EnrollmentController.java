package org.example.studentcoursemanagement.controller;

import jakarta.validation.Valid;
import org.example.studentcoursemanagement.dto.EnrollmentRequestDTO;
import org.example.studentcoursemanagement.dto.EnrollmentResponseDTO;
import org.example.studentcoursemanagement.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // Create Enrollment
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(
            @Valid @RequestBody EnrollmentRequestDTO request) {

        return ResponseEntity.ok(
                enrollmentService.createEnrollment(request)
        );
    }

    // Get All Enrollments
    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments() {

        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments()
        );
    }

    // Get Enrollment By ID
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(
            @PathVariable Long id) {

        return enrollmentService.getEnrollmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get Enrollments By Student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByStudent(studentId)
        );
    }

    // Get Enrollments By Course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByCourse(courseId)
        );
    }

    // Update Enrollment
    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentRequestDTO request) {

        return ResponseEntity.ok(
                enrollmentService.updateEnrollment(id, request)
        );
    }

    // Delete Enrollment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Long id) {

        enrollmentService.deleteEnrollment(id);

        return ResponseEntity.noContent().build();
    }
}
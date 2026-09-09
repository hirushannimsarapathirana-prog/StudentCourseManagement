package org.example.studentcoursemanagement.controller;

import org.example.studentcoursemanagement.entity.Enrollment;
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

    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {

        return ResponseEntity.ok(
                enrollmentService.createEnrollment(studentId, courseId)
        );
    }

    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(
            @PathVariable Long id) {

        return enrollmentService.getEnrollmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByStudent(studentId)
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByCourse(courseId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> updateEnrollment(
            @PathVariable Long id,
            @RequestParam Long studentId,
            @RequestParam Long courseId) {

        return ResponseEntity.ok(
                enrollmentService.updateEnrollment(
                        id,
                        studentId,
                        courseId
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Long id) {

        enrollmentService.deleteEnrollment(id);

        return ResponseEntity.noContent().build();
    }
}
package org.example.studentcoursemanagement.controller;

import jakarta.validation.Valid;
import org.example.studentcoursemanagement.dto.CourseRequestDTO;
import org.example.studentcoursemanagement.dto.CourseResponseDTO;
import org.example.studentcoursemanagement.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Create Course
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(
            @Valid @RequestBody CourseRequestDTO request) {

        return ResponseEntity.ok(
                courseService.createCourse(request)
        );
    }

    // Get All Courses
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {

        return ResponseEntity.ok(
                courseService.getAllCourses()
        );
    }

    // Get Course By ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(
            @PathVariable Long id) {

        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update Course
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO request) {

        return ResponseEntity.ok(
                courseService.updateCourse(id, request)
        );
    }

    // Delete Course
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id) {

        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }
}
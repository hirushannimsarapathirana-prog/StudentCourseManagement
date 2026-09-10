package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.CourseRequestDTO;
import org.example.studentcoursemanagement.dto.CourseResponseDTO;
import org.example.studentcoursemanagement.entity.Course;
import org.example.studentcoursemanagement.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // CREATE
    public CourseResponseDTO createCourse(CourseRequestDTO request) {

        Course course = new Course();

        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setDescription(request.getDescription());
        course.setFee(request.getFee());

        Course savedCourse = courseRepository.save(course);

        return convertToResponseDTO(savedCourse);
    }

    // GET ALL
    public List<CourseResponseDTO> getAllCourses() {

        return courseRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY ID
    public Optional<CourseResponseDTO> getCourseById(Long id) {

        return courseRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    // UPDATE
    public CourseResponseDTO updateCourse(
            Long id,
            CourseRequestDTO request) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setDescription(request.getDescription());
        course.setFee(request.getFee());

        Course updatedCourse = courseRepository.save(course);

        return convertToResponseDTO(updatedCourse);
    }

    // DELETE
    public void deleteCourse(Long id) {

        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found");
        }

        courseRepository.deleteById(id);
    }

    // ENTITY → RESPONSE DTO
    private CourseResponseDTO convertToResponseDTO(Course course) {

        return new CourseResponseDTO(
                course.getId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getDescription(),
                course.getFee()
        );
    }
}

package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.EnrollmentRequestDTO;
import org.example.studentcoursemanagement.dto.EnrollmentResponseDTO;
import org.example.studentcoursemanagement.entity.Course;
import org.example.studentcoursemanagement.entity.Enrollment;
import org.example.studentcoursemanagement.entity.Student;
import org.example.studentcoursemanagement.repository.CourseRepository;
import org.example.studentcoursemanagement.repository.EnrollmentRepository;
import org.example.studentcoursemanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // CREATE
    public EnrollmentResponseDTO createEnrollment(
            EnrollmentRequestDTO request) {

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());

        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        return convertToResponseDTO(savedEnrollment);
    }

    // GET ALL
    public List<EnrollmentResponseDTO> getAllEnrollments() {

        return enrollmentRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY ID
    public Optional<EnrollmentResponseDTO> getEnrollmentById(Long id) {

        return enrollmentRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    // GET BY STUDENT
    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(
            Long studentId) {

        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY COURSE
    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(
            Long courseId) {

        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // UPDATE
    public EnrollmentResponseDTO updateEnrollment(
            Long id,
            EnrollmentRequestDTO request) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Enrollment not found"));

        Student student =
                studentRepository.findById(request.getStudentId())
                        .orElseThrow(() ->
                                new RuntimeException("Student not found"));

        Course course =
                courseRepository.findById(request.getCourseId())
                        .orElseThrow(() ->
                                new RuntimeException("Course not found"));

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment updatedEnrollment =
                enrollmentRepository.save(enrollment);

        return convertToResponseDTO(updatedEnrollment);
    }

    // DELETE
    public void deleteEnrollment(Long id) {

        if (!enrollmentRepository.existsById(id)) {
            throw new RuntimeException("Enrollment not found");
        }

        enrollmentRepository.deleteById(id);
    }

    // ENTITY → RESPONSE DTO
    private EnrollmentResponseDTO convertToResponseDTO(
            Enrollment enrollment) {

        String studentName =
                enrollment.getStudent().getFirstName()
                        + " "
                        + enrollment.getStudent().getLastName();

        return new EnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStudent().getId(),
                studentName,
                enrollment.getCourse().getId(),
                enrollment.getCourse().getCourseName()
        );
    }
}
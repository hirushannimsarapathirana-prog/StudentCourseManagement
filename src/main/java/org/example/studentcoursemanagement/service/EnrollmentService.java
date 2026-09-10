package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.EnrollmentRequestDTO;
import org.example.studentcoursemanagement.dto.EnrollmentResponseDTO;
import org.example.studentcoursemanagement.entity.Course;
import org.example.studentcoursemanagement.entity.Enrollment;
import org.example.studentcoursemanagement.entity.Student;
import org.example.studentcoursemanagement.entity.User;
import org.example.studentcoursemanagement.repository.CourseRepository;
import org.example.studentcoursemanagement.repository.EnrollmentRepository;
import org.example.studentcoursemanagement.repository.StudentRepository;
import org.example.studentcoursemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // CREATE ENROLLMENT
    // ADMIN → any student
    // STUDENT → own student only
    // ==========================================

    public EnrollmentResponseDTO createEnrollment(
            EnrollmentRequestDTO request,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Student student = studentRepository.findById(
                request.getStudentId()
        ).orElseThrow(() ->
                new RuntimeException("Student not found"));

        // If logged-in user is STUDENT,
        // check whether this is their own student profile
        if ("STUDENT".equalsIgnoreCase(user.getRole())) {

            if (user.getStudent() == null ||
                    !user.getStudent()
                            .getId()
                            .equals(student.getId())) {

                throw new RuntimeException(
                        "You can enroll only yourself"
                );
            }
        }

        Course course = courseRepository.findById(
                request.getCourseId()
        ).orElseThrow(() ->
                new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());

        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        return convertToResponseDTO(savedEnrollment);
    }


    // ==========================================
    // GET ALL ENROLLMENTS
    // ADMIN ONLY
    // ==========================================

    public List<EnrollmentResponseDTO> getAllEnrollments() {

        return enrollmentRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    // ==========================================
    // GET ENROLLMENT BY ID
    // ==========================================

    public Optional<EnrollmentResponseDTO> getEnrollmentById(
            Long id) {

        return enrollmentRepository.findById(id)
                .map(this::convertToResponseDTO);
    }


    // ==========================================
    // GET ENROLLMENTS BY STUDENT
    // ==========================================

    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(
            Long studentId) {

        return enrollmentRepository
                .findByStudentId(studentId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    // ==========================================
    // GET ENROLLMENTS BY COURSE
    // ADMIN ONLY
    // ==========================================

    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(
            Long courseId) {

        return enrollmentRepository
                .findByCourseId(courseId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    // ==========================================
    // UPDATE ENROLLMENT
    // ADMIN ONLY
    // ==========================================

    public EnrollmentResponseDTO updateEnrollment(
            Long id,
            EnrollmentRequestDTO request) {

        Enrollment enrollment =
                enrollmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Enrollment not found"
                                ));

        Student student =
                studentRepository.findById(
                        request.getStudentId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Student not found"
                        ));

        Course course =
                courseRepository.findById(
                        request.getCourseId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Course not found"
                        ));

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment updatedEnrollment =
                enrollmentRepository.save(enrollment);

        return convertToResponseDTO(updatedEnrollment);
    }


    // ==========================================
    // DELETE ENROLLMENT
    // ADMIN ONLY
    // ==========================================

    public void deleteEnrollment(Long id) {

        if (!enrollmentRepository.existsById(id)) {

            throw new RuntimeException(
                    "Enrollment not found"
            );
        }

        enrollmentRepository.deleteById(id);
    }


    // ==========================================
    // CHECK OWNERSHIP
    // ==========================================

    public boolean isOwnEnrollment(
            Long enrollmentId,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        if (user.getStudent() == null) {
            return false;
        }

        return enrollmentRepository.findById(enrollmentId)
                .map(enrollment ->
                        enrollment.getStudent()
                                .getId()
                                .equals(
                                        user.getStudent().getId()
                                )
                )
                .orElse(false);
    }


    // ==========================================
    // CHECK STUDENT OWNERSHIP
    // ==========================================

    public boolean isOwnStudent(
            Long studentId,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        if (user.getStudent() == null) {
            return false;
        }

        return user.getStudent()
                .getId()
                .equals(studentId);
    }


    // ==========================================
    // CONVERT ENTITY → DTO
    // ==========================================

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
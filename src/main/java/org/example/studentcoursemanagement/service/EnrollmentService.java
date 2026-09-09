package org.example.studentcoursemanagement.service;

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

    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository, CourseRepository courseRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public Enrollment createEnrollment(Long studentId, Long courseId) {

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public Enrollment updateEnrollment(Long id, Long studentId, Long courseId) {

        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return enrollmentRepository.save(enrollment);
    }

    public void deleteEnrollment(Long id) {

        if (!enrollmentRepository.existsById(id)) {
            throw new RuntimeException("Enrollment not found");
        }

        enrollmentRepository.deleteById(id);
    }
}
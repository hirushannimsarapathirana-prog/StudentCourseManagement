package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.StudentRequestDTO;
import org.example.studentcoursemanagement.dto.StudentResponseDTO;
import org.example.studentcoursemanagement.entity.Student;
import org.example.studentcoursemanagement.entity.User;
import org.example.studentcoursemanagement.repository.StudentRepository;
import org.example.studentcoursemanagement.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentService(
            StudentRepository studentRepository,
            UserRepository userRepository) {

        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public StudentResponseDTO createStudent(StudentRequestDTO request) {

        Student student = new Student();

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        Student savedStudent = studentRepository.save(student);

        return convertToResponseDTO(savedStudent);
    }

    public List<StudentResponseDTO> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public Optional<StudentResponseDTO> getStudentById(Long id) {

        return studentRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    public StudentResponseDTO updateStudent(
            Long id,
            StudentRequestDTO request) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        Student updatedStudent =
                studentRepository.save(student);

        return convertToResponseDTO(updatedStudent);
    }

    public void deleteStudent(Long id) {

        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found");
        }

        studentRepository.deleteById(id);
    }

    // Check whether the logged-in student owns this profile
    public boolean isOwnProfile(
            Long studentId,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getStudent() == null) {
            return false;
        }

        return user.getStudent()
                .getId()
                .equals(studentId);
    }

    private StudentResponseDTO convertToResponseDTO(
            Student student) {

        return new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPhone(),
                student.getAddress()
        );
    }
}
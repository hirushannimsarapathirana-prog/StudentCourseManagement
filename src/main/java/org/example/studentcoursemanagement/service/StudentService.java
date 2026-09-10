package org.example.studentcoursemanagement.service;

import org.example.studentcoursemanagement.dto.StudentRequestDTO;
import org.example.studentcoursemanagement.dto.StudentResponseDTO;
import org.example.studentcoursemanagement.entity.Student;
import org.example.studentcoursemanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // CREATE
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

    // GET ALL
    public List<StudentResponseDTO> getAllStudents() {

        return studentRepository.findAll().stream().map(this::convertToResponseDTO).toList();
    }

    // GET BY ID
    public Optional<StudentResponseDTO> getStudentById(Long id) {

        return studentRepository.findById(id).map(this::convertToResponseDTO);
    }

    // UPDATE
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {

        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        Student updatedStudent = studentRepository.save(student);

        return convertToResponseDTO(updatedStudent);
    }

    // DELETE
    public void deleteStudent(Long id) {

        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found");
        }

        studentRepository.deleteById(id);
    }

    // ENTITY → RESPONSE DTO
    private StudentResponseDTO convertToResponseDTO(Student student) {

        return new StudentResponseDTO(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail(), student.getPhone(), student.getAddress());
    }
}
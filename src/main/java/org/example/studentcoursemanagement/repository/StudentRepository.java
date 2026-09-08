package org.example.studentcoursemanagement.repository;

import org.example.studentcoursemanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
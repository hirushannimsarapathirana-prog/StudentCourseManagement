package org.example.studentcoursemanagement.repository;

import org.example.studentcoursemanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
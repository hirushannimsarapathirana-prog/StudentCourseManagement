package org.example.studentcoursemanagement.dto;

import java.time.LocalDate;

public class EnrollmentResponseDTO {

    private Long id;
    private LocalDate enrollmentDate;

    private Long studentId;
    private String studentName;

    private Long courseId;
    private String courseName;

    public EnrollmentResponseDTO() {
    }

    public EnrollmentResponseDTO(
            Long id,
            LocalDate enrollmentDate,
            Long studentId,
            String studentName,
            Long courseId,
            String courseName) {

        this.id = id;
        this.enrollmentDate = enrollmentDate;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
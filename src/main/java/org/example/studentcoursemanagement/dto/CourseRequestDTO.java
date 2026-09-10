package org.example.studentcoursemanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CourseRequestDTO {

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 0, message = "Course fee cannot be negative")
    private double fee;

    public CourseRequestDTO() {
    }

    public CourseRequestDTO(
            String courseName,
            String courseCode,
            String description,
            double fee) {

        this.courseName = courseName;
        this.courseCode = courseCode;
        this.description = description;
        this.fee = fee;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
package org.example.studentcoursemanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    // Password is optional when updating a user
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    private Long studentId;

    public UserRequestDTO() {
    }

    public UserRequestDTO(
            String username,
            String password,
            String role,
            Long studentId) {

        this.username = username;
        this.password = password;
        this.role = role;
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
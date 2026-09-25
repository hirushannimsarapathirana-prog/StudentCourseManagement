# 🎓 Student Course Management System

A web-based **Student Course Management System** developed using **Java Spring Boot** to manage students, courses, enrollments, authentication, and user profiles.

The system provides separate functionality for **Administrators** and **Students**, with role-based access control and JWT-based authentication.

---

## 📌 Project Overview

The Student Course Management System is designed to provide a centralized platform for managing students and courses.

The system supports two main user roles:

* 👨‍💼 **Administrator**
* 👨‍🎓 **Student**

Administrators can manage students, courses, and enrollments, while students can view available courses, enroll in courses, manage their profile, and view their own course enrollments.

---

## ✨ Features

### 🔐 Authentication

* User login
* JWT authentication
* Secure password handling
* Role-based authorization
* Protected API endpoints

### 👨‍💼 Administrator Features

Administrators can:

* ➕ Add students
* 📋 View students
* 🔍 View student details
* ✏️ Update student information
* 🗑️ Delete students
* ➕ Add courses
* 📋 View courses
* ✏️ Update courses
* 🗑️ Delete courses
* 📚 Manage enrollments
* 👥 Manage student-course relationships

### 👨‍🎓 Student Features

Students can:

* 👤 View their profile
* ✏️ Update their profile
* 📚 View available courses
* 🔎 View course details
* 📝 Enroll in courses
* 📋 View their enrollments
* 🎓 View their enrolled courses

---

## 🛡️ Role-Based Access

The application separates functionality according to the authenticated user's role.

```text
                    ┌─────────────────────┐
                    │        LOGIN        │
                    └──────────┬──────────┘
                               │
                         JWT Authentication
                               │
                 ┌─────────────┴─────────────┐
                 │                           │
                 ▼                           ▼
        ┌─────────────────┐         ┌─────────────────┐
        │      ADMIN      │         │     STUDENT     │
        └────────┬────────┘         └────────┬────────┘
                 │                           │
       ┌─────────┼─────────┐          ┌──────┼──────┐
       │         │         │          │      │      │
       ▼         ▼         ▼          ▼      ▼      ▼
    Students  Courses  Enrollments  Profile Courses Enrollments
```

---

## 🏗️ System Architecture

The project follows a layered Spring Boot architecture.

```text
┌──────────────────────────────────────────┐
│                 FRONTEND                 │
│            HTML / CSS / JavaScript       │
└────────────────────┬─────────────────────┘
                     │
                     │ HTTP / REST API
                     ▼
┌──────────────────────────────────────────┐
│               CONTROLLER                 │
│         REST API Endpoints               │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│                 SERVICE                  │
│          Business Logic                  │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│               REPOSITORY                 │
│          Spring Data JPA                │
└────────────────────┬─────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────┐
│                DATABASE                 │
│                  MySQL                  │
└──────────────────────────────────────────┘
```

---

## 🛠️ Technologies Used

| Technology         | Purpose                        |
| ------------------ | ------------------------------ |
| ☕ Java 21          | Backend development            |
| 🌱 Spring Boot     | Backend framework              |
| 🔐 Spring Security | Authentication & authorization |
| 🎟️ JWT            | Token-based authentication     |
| 🗄️ MySQL          | Database                       |
| 🧩 Spring Data JPA | Database access                |
| 🛢️ Hibernate      | ORM                            |
| 📦 Maven           | Dependency management          |
| 🌐 HTML5           | Frontend structure             |
| 🎨 CSS3            | Frontend styling               |
| ⚡ JavaScript       | Frontend functionality         |
| 🔄 REST API        | Frontend/backend communication |
| 🐙 Git & GitHub    | Version control                |
| 💻 IntelliJ IDEA   | Development environment        |

---

## 📂 Project Structure

```text
StudentCourseManagement/
│
├── Front End/
│   ├── HTML files
│   ├── CSS files
│   └── JavaScript files
│
├── src/
│   └── main/
│       ├── java/
│       │   └── org/
│       │       └── example/
│       │           └── studentcoursemanagement/
│       │
│       └── resources/
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🔑 Authentication Flow

The system uses JWT-based authentication.

```text
User
 │
 ▼
Login
 │
 ▼
Authentication
 │
 ▼
Username + Password Validation
 │
 ▼
JWT Token Generated
 │
 ▼
Token Stored by Frontend
 │
 ▼
Token Sent with API Requests
 │
 ▼
Spring Security
 │
 ▼
Role Authorization
```

---

## 📚 Course Management

### Administrator

Administrators can manage the complete course collection.

```text
Add Course
     ↓
View Courses
     ↓
Update Course
     ↓
Delete Course
```

### Student

Students can:

* View available courses
* View course information
* Enroll in courses
* View their enrolled courses

---

## 👥 Student Management

Administrators can manage student records.

### Operations

```text
CREATE
  ↓
READ
  ↓
UPDATE
  ↓
DELETE
```

Student profile functionality also allows students to view and update their own information.

---

## 📝 Enrollment Management

The enrollment module connects students with courses.

```text
Student
   │
   │ Enrollment
   ▼
Course
```

Administrators can manage enrollment records, while students can access their own enrollment information.

---

## 🌐 Frontend Pages

The frontend contains separate pages/views for different parts of the application.

```text
Login
 │
 ▼
Application
 │
 ├── Students
 │
 ├── Courses
 │
 ├── Available Courses
 │
 ├── Enrollments
 │
 ├── My Courses
 │
 └── Profile
```

Navigation and functionality are controlled according to the user's role.

---

## 🔌 REST API

The backend exposes REST API endpoints for communication with the frontend.

Typical HTTP operations include:

```text
GET       → Retrieve data
POST      → Create data
PUT       → Update data
DELETE    → Delete data
```

Authentication-protected endpoints require a valid JWT token.

---

## ⚙️ Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/hirushannimsarapathirana-prog/StudentCourseManagement.git
```

### 2. Open the Project

Open the project using **IntelliJ IDEA**.

Make sure Maven recognizes the `pom.xml` file.

### 3. Configure the Database

Create the required MySQL database.

Configure the database connection in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_course_management
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> Replace the database username and password with your local credentials. Do not commit real passwords to GitHub.

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Backend

Run the Spring Boot main application from IntelliJ IDEA.

The backend will start on the configured server port.

### 6. Run the Frontend

Open the files inside:

```text
Front End/
```

using a local development server such as **VS Code Live Server**.

---

## 🧪 Testing

The REST APIs can be tested using tools such as:

* Postman
* Browser Developer Tools
* Frontend application

Authentication should be tested before accessing protected endpoints.

---

## 🔒 Security

The application uses:

* JWT authentication
* Spring Security
* Role-based authorization
* Password protection
* Protected API endpoints
* CORS configuration for frontend communication

The security layer ensures that users can only access functionality permitted for their assigned role.

---

## 🎯 Project Objectives

The main objectives of this project are:

* Build a real-world Spring Boot application
* Implement REST APIs
* Understand Spring Security
* Implement JWT authentication
* Implement role-based authorization
* Work with MySQL databases
* Use Spring Data JPA
* Implement CRUD operations
* Connect frontend and backend
* Practice software architecture
* Manage source code using Git and GitHub

---

## 📖 Learning Outcomes

Through this project, I gained practical experience in:

* Java 21
* Spring Boot
* Spring Security
* JWT authentication
* REST API development
* Spring Data JPA
* Hibernate
* MySQL
* HTML, CSS and JavaScript
* Frontend/backend integration
* Role-based access control
* Maven
* Git and GitHub

---

## 🔮 Future Improvements

Possible future improvements include:

* 📊 Admin dashboard
* 📈 Course analytics
* 🔔 Student notifications
* 📧 Email notifications
* 📅 Course scheduling
* 🔎 Advanced search and filtering
* 📄 Pagination
* 📖 Swagger/OpenAPI documentation
* 🧪 Automated testing
* 🐳 Docker support
* ☁️ Cloud deployment
* 📱 Mobile application

---

## 📸 Screenshots

Add screenshots of the application here.

Recommended screenshots:

```text
1. Login Page
2. Admin Dashboard
3. Student Management
4. Course Management
5. Available Courses
6. Enrollment Management
7. My Courses
8. Student Profile
```

---

## 👨‍💻 Author

### Hirushan Nimsara Pathirana

Software Development / Computer Science Student

GitHub:

https://github.com/hirushannimsarapathirana-prog

---

## 📄 License

This project was developed for **educational and academic purposes**.

---

## ⭐ Repository

If you find this project useful, feel free to ⭐ the repository.

**GitHub Repository**

https://github.com/hirushannimsarapathirana-prog/StudentCourseManagement

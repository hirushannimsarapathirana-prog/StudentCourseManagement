// ======================================================
// STUDENT COURSE MANAGEMENT SYSTEM
// app.js
// ======================================================

const API_BASE = "http://localhost:8080/api";

let courses = [];
let enrollments = [];

let currentUser = null;
let currentRole = null;


// ======================================================
// PAGE LOAD
// ======================================================

document.addEventListener("DOMContentLoaded", function () {

    const token = localStorage.getItem("token");

    if (token) {

        const user = decodeJwt(token);

        if (user) {

            currentUser = user;
            currentRole = getUserRole(user);

            console.log("JWT Payload:", user);
            console.log("Detected Role:", currentRole);

            if (currentRole) {
                showApplication();

                loadCourses();
                loadEnrollments();

                if (isStudent()) {
                    loadProfile();
                }

                if (isAdmin()) {
                    loadStudents();
                }

            } else {
                console.error(
                    "User role could not be detected from JWT."
                );

                logout();
            }

        } else {
            logout();
        }

    } else {
        showLogin();
    }

});


// ======================================================
// LOGIN
// ======================================================

const loginForm = document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const username =
            document.getElementById("loginUsername").value.trim();

        const password =
            document.getElementById("loginPassword").value;

        const message =
            document.getElementById("loginMessage");

        message.textContent = "Logging in...";
        message.className = "message";

        try {

            const response = await fetch(
                `${API_BASE}/auth/login`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        username: username,
                        password: password
                    })
                }
            );

            const result = await response.text();

            if (!response.ok) {
                throw new Error(
                    result || "Invalid username or password."
                );
            }

            const token = extractToken(result);

            if (!token) {
                throw new Error(
                    "Login successful, but JWT token was not returned."
                );
            }

            localStorage.setItem("token", token);

            currentUser = decodeJwt(token);

            if (!currentUser) {
                throw new Error("Invalid JWT token.");
            }

            currentRole = getUserRole(currentUser);

            console.log("JWT Payload:", currentUser);
            console.log("Detected Role:", currentRole);

            if (!currentRole) {

                console.error(
                    "JWT does not contain a recognizable role."
                );

                throw new Error(
                    "User role could not be detected from JWT. Check the browser console."
                );
            }

            showApplication();

            await loadCourses();
            await loadEnrollments();

            if (isStudent()) {
                await loadProfile();
            }

            if (isAdmin()) {
                await loadStudents();
            }

            showToast(
                `Welcome ${getUsername()}!`
            );

        } catch (error) {

            console.error("Login error:", error);

            localStorage.removeItem("token");

            message.textContent =
                error.message || "Login failed.";

            message.className =
                "message error";
        }

    });

}


// ======================================================
// EXTRACT TOKEN
// ======================================================

function extractToken(result) {

    if (!result) {
        return null;
    }

    result = result.trim();

    // Plain JWT
    if (
        result.startsWith("eyJ") &&
        result.split(".").length === 3
    ) {
        return result;
    }

    try {

        const data = JSON.parse(result);

        return (
            data.token ||
            data.accessToken ||
            data.jwt ||
            data.access_token ||
            null
        );

    } catch (error) {

        return null;
    }
}


// ======================================================
// DECODE JWT
// ======================================================

function decodeJwt(token) {

    try {

        if (!token) {
            return null;
        }

        const parts = token.split(".");

        if (parts.length !== 3) {
            console.error("Invalid JWT format.");
            return null;
        }

        const base64Url = parts[1];

        const base64 = base64Url
            .replace(/-/g, "+")
            .replace(/_/g, "/");

        const jsonPayload =
            decodeURIComponent(
                atob(base64)
                    .split("")
                    .map(function (character) {

                        return "%" +
                            (
                                "00" +
                                character
                                    .charCodeAt(0)
                                    .toString(16)
                            ).slice(-2);

                    })
                    .join("")
            );

        const payload =
            JSON.parse(jsonPayload);

        console.log(
            "Decoded JWT:",
            payload
        );

        return payload;

    } catch (error) {

        console.error(
            "JWT decoding error:",
            error
        );

        return null;
    }
}


// ======================================================
// GET USER ROLE
// ======================================================

function getUserRole(user) {

    if (!user) {
        return null;
    }

    let role =
        user.role ??
        user.roles ??
        user.authority ??
        user.authorities ??
        user.userRole ??
        user.user_role ??
        user["http://schemas.microsoft.com/ws/2008/06/identity/claims/role"];

    // roles array
    if (Array.isArray(role)) {

        if (role.length === 0) {
            return null;
        }

        role = role[0];

        if (
            typeof role === "object" &&
            role !== null
        ) {

            role =
                role.authority ??
                role.role ??
                role.name ??
                role.value;
        }
    }

    // role object
    if (
        typeof role === "object" &&
        role !== null
    ) {

        role =
            role.authority ??
            role.role ??
            role.name ??
            role.value;
    }

    if (!role) {
        return null;
    }

    role = String(role)
        .toUpperCase()
        .trim();

    // ROLE_ADMIN
    // ADMIN
    if (role.includes("ADMIN")) {
        return "ADMIN";
    }

    // ROLE_STUDENT
    // STUDENT
    if (role.includes("STUDENT")) {
        return "STUDENT";
    }

    return role.replace(
        /^ROLE_/,
        ""
    );
}


// ======================================================
// GET USERNAME
// ======================================================

function getUsername() {

    return (
        currentUser?.username ||
        currentUser?.sub ||
        currentUser?.name ||
        currentUser?.email ||
        "User"
    );
}


// ======================================================
// GET USER ID
// ======================================================

function getUserId() {

    return (
        currentUser?.id ||
        currentUser?.userId ||
        currentUser?.studentId ||
        currentUser?.user_id ||
        currentUser?.student_id ||
        currentUser?.student?.id ||
        currentUser?.user?.id ||
        null
    );
}


// ======================================================
// ROLE CHECK
// ======================================================

function isAdmin() {

    return currentRole === "ADMIN";
}


function isStudent() {

    return currentRole === "STUDENT";
}


// ======================================================
// SHOW LOGIN
// ======================================================

function showLogin() {

    const loginPage =
        document.getElementById("loginPage");

    const appPage =
        document.getElementById("appPage");

    if (loginPage) {
        loginPage.classList.remove("hidden");
    }

    if (appPage) {
        appPage.classList.add("hidden");
    }
}


// ======================================================
// SHOW APPLICATION
// ======================================================

function showApplication() {

    const loginPage =
        document.getElementById("loginPage");

    const appPage =
        document.getElementById("appPage");

    if (loginPage) {
        loginPage.classList.add("hidden");
    }

    if (appPage) {
        appPage.classList.remove("hidden");
    }

    updateUserInterface();

    showPage("dashboard");
}


// ======================================================
// UPDATE USER INTERFACE
// ======================================================

function updateUserInterface() {

    const username =
        getUsername();

    // Username
    document.querySelectorAll(
        "#sidebarUsername, #topUsername"
    ).forEach(function (element) {

        element.textContent =
            username;

    });


    // Role
    document.querySelectorAll(
        "#sidebarRole, #topRole"
    ).forEach(function (element) {

        element.textContent =
            currentRole || "USER";

    });


    // Dashboard role
    const dashboardRole =
        document.getElementById(
            "dashboardRole"
        );

    if (dashboardRole) {

        dashboardRole.textContent =
            currentRole || "USER";
    }


    // Info role
    const infoRole =
        document.getElementById(
            "infoRole"
        );

    if (infoRole) {

        infoRole.textContent =
            currentRole || "USER";
    }


    // Avatar
    document.querySelectorAll(
        ".avatar"
    ).forEach(function (avatar) {

        avatar.textContent =
            username
                .charAt(0)
                .toUpperCase();

    });


    // Admin navigation
    document.querySelectorAll(
        ".admin-only"
    ).forEach(function (element) {

        element.style.display =
            isAdmin()
                ? ""
                : "none";

    });


    // Student navigation
    document.querySelectorAll(
        ".student-only"
    ).forEach(function (element) {

        element.style.display =
            isStudent()
                ? ""
                : "none";

    });


    // Enrollment title
    const enrollmentTitle =
        document.getElementById(
            "enrollmentTitle"
        );

    if (enrollmentTitle) {

        enrollmentTitle.textContent =
            isStudent()
                ? "My Enrollments"
                : "Enrollment Management";
    }


    // Enrollment subtitle
    const enrollmentSubtitle =
        document.getElementById(
            "enrollmentSubtitle"
        );

    if (enrollmentSubtitle) {

        enrollmentSubtitle.textContent =
            isStudent()
                ? "View your course enrollments"
                : "Manage student course enrollments";
    }


    // Student access information
    const studentAccessInfo =
        document.getElementById(
            "studentAccessInfo"
        );

    if (studentAccessInfo) {

        studentAccessInfo.style.display =
            isStudent()
                ? ""
                : "none";
    }

}


// ======================================================
// API FETCH
// ======================================================

async function apiFetch(
    url,
    options = {}
) {

    const token =
        localStorage.getItem("token");

    if (!token) {

        logout();

        throw new Error(
            "Session expired. Please login again."
        );
    }


    const headers = {
        "Content-Type":
            "application/json",

        ...(options.headers || {}),

        "Authorization":
            `Bearer ${token}`
    };


    const response =
        await fetch(
            url,
            {
                ...options,
                headers: headers
            }
        );


    if (response.status === 401) {

        logout();

        throw new Error(
            "Session expired. Please login again."
        );
    }


    if (response.status === 403) {

        throw new Error(
            "You do not have permission to perform this action."
        );
    }


    return response;
}


// ======================================================
// READ API RESPONSE
// ======================================================

async function readResponse(response) {

    const text =
        await response.text();

    if (!text) {
        return null;
    }

    try {

        return JSON.parse(text);

    } catch (error) {

        return text;
    }
}


// ======================================================
// SHOW PAGE
// ======================================================

function showPage(pageName) {

    // Security checks

    if (
        pageName === "students" &&
        !isAdmin()
    ) {
        showToast(
            "Only administrators can access Student Management."
        );
        return;
    }


    if (
        pageName === "availableCourses" &&
        !isStudent()
    ) {
        showToast(
            "This page is available to students only."
        );
        return;
    }


    if (
        pageName === "myCourses" &&
        !isStudent()
    ) {
        showToast(
            "This page is available to students only."
        );
        return;
    }


    if (
        pageName === "profile" &&
        !isStudent()
    ) {
        showToast(
            "This page is available to students only."
        );
        return;
    }


    // Hide pages

    document.querySelectorAll(
        ".page"
    ).forEach(function (page) {

        page.classList.add("hidden");

    });


    // Show selected page

    const page =
        document.getElementById(
            `${pageName}Page`
        );

    if (page) {

        page.classList.remove("hidden");
    }


    // Navigation active state

    document.querySelectorAll(
        ".nav-item"
    ).forEach(function (item) {

        item.classList.remove(
            "active"
        );

    });


    document.querySelectorAll(
        ".nav-item"
    ).forEach(function (item) {

        const onclick =
            item.getAttribute(
                "onclick"
            );

        if (
            onclick &&
            onclick.includes(
                `'${pageName}'`
            )
        ) {

            item.classList.add(
                "active"
            );
        }

    });


    // Page title

    const titles = {

        dashboard: {
            title: "Dashboard",
            subtitle:
                "Student Course Management System"
        },

        students: {
            title: "Student Management",
            subtitle:
                "Manage registered students"
        },

        courses: {
            title: "Course Management",
            subtitle:
                "Manage available courses"
        },

        availableCourses: {
            title: "Available Courses",
            subtitle:
                "Browse and enroll in courses"
        },

        enrollments: {
            title:
                isStudent()
                    ? "My Enrollments"
                    : "Enrollment Management",

            subtitle:
                isStudent()
                    ? "View your course enrollments"
                    : "Manage student enrollments"
        },

        myCourses: {
            title: "My Courses",
            subtitle:
                "Courses you are enrolled in"
        },

        profile: {
            title: "My Profile",
            subtitle:
                "View and update your profile"
        }
    };


    const pageInfo =
        titles[pageName];


    if (pageInfo) {

        const title =
            document.getElementById(
                "pageTitle"
            );

        const subtitle =
            document.getElementById(
                "pageSubtitle"
            );


        if (title) {
            title.textContent =
                pageInfo.title;
        }

        if (subtitle) {
            subtitle.textContent =
                pageInfo.subtitle;
        }
    }


    // Page-specific loading

    if (
        pageName === "courses" ||
        pageName === "availableCourses"
    ) {

        loadCourses();
    }


    if (
        pageName === "enrollments"
    ) {

        loadEnrollments();
    }


    if (
        pageName === "myCourses"
    ) {

        loadMyCourses();
    }


    if (
        pageName === "profile"
    ) {

        loadProfile();
    }


    if (
        pageName === "students"
    ) {

        loadStudents();
    }

}


// ======================================================
// LOGOUT
// ======================================================

function logout() {

    localStorage.removeItem(
        "token"
    );

    currentUser = null;
    currentRole = null;

    courses = [];
    enrollments = [];

    showLogin();

}


// ======================================================
// TOAST
// ======================================================

function showToast(message) {

    const toast =
        document.getElementById(
            "toast"
        );

    if (!toast) {
        return;
    }

    toast.textContent =
        message;

    toast.classList.add(
        "show"
    );


    setTimeout(function () {

        toast.classList.remove(
            "show"
        );

    }, 3000);

}


// ======================================================
// COURSES
// ======================================================

async function loadCourses() {

    try {

        const response =
            await apiFetch(
                `${API_BASE}/courses`
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load courses."
            );
        }


        const data =
            await readResponse(
                response
            );


        courses =
            Array.isArray(data)
                ? data
                : [];


        renderCourses(
            courses
        );

        renderAvailableCourses(
            courses
        );


        const totalCourses =
            document.getElementById(
                "totalCourses"
            );

        if (totalCourses) {

            totalCourses.textContent =
                courses.length;
        }


        populateCourseSelect();

    } catch (error) {

        console.error(
            "Load courses error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// RENDER COURSES
// ======================================================

function renderCourses(list) {

    const tableBody =
        document.getElementById(
            "courseTableBody"
        );

    if (!tableBody) {
        return;
    }


    if (!list.length) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    <div class="empty-state">
                        <div class="empty-state-icon">
                            📚
                        </div>
                        <h3>No courses found</h3>
                        <p>No courses are currently available.</p>
                    </div>
                </td>
            </tr>
        `;

        return;
    }


    tableBody.innerHTML =
        list.map(function (course) {

            const id =
                course.id ??
                course.courseId;

            const name =
                course.name ??
                course.courseName ??
                "Unnamed Course";

            const description =
                course.description ??
                "No description";


            let actions = `
                <button
                    class="action-btn action-view"
                    onclick="viewCourse(${id})">
                    View
                </button>
            `;


            if (isAdmin()) {

                actions += `
                    <button
                        class="action-btn action-edit"
                        onclick="editCourse(${id})">
                        Edit
                    </button>

                    <button
                        class="action-btn action-delete"
                        onclick="deleteCourse(${id})">
                        Delete
                    </button>
                `;
            }


            return `
                <tr>

                    <td>${id}</td>

                    <td>
                        <strong>${escapeHtml(name)}</strong>
                    </td>

                    <td>
                        ${escapeHtml(description)}
                    </td>

                    <td>
                        <span class="badge badge-admin">
                            Active
                        </span>
                    </td>

                    <td>
                        <div class="actions">
                            ${actions}
                        </div>
                    </td>

                </tr>
            `;

        }).join("");

}


// ======================================================
// COURSE SEARCH
// ======================================================

function filterCourses() {

    const searchInput =
        document.getElementById(
            "courseSearch"
        );

    if (!searchInput) {
        return;
    }


    const search =
        searchInput.value
            .toLowerCase()
            .trim();


    const filtered =
        courses.filter(function (course) {

            const id =
                String(
                    course.id ??
                    course.courseId ??
                    ""
                );

            const name =
                String(
                    course.name ??
                    course.courseName ??
                    ""
                );

            const description =
                String(
                    course.description ??
                    ""
                );


            return (
                id.toLowerCase().includes(search) ||
                name.toLowerCase().includes(search) ||
                description.toLowerCase().includes(search)
            );

        });


    renderCourses(
        filtered
    );

}


// ======================================================
// AVAILABLE COURSE CARDS
// ======================================================

function renderAvailableCourses(list) {

    const container =
        document.getElementById(
            "availableCoursesGrid"
        );

    if (!container) {
        return;
    }


    if (!list.length) {

        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">
                    📚
                </div>
                <h3>No courses available</h3>
                <p>There are currently no courses to display.</p>
            </div>
        `;

        return;
    }


    container.innerHTML =
        list.map(function (course) {

            const id =
                course.id ??
                course.courseId;

            const name =
                course.name ??
                course.courseName ??
                "Unnamed Course";

            const description =
                course.description ??
                "No description available.";


            return `
                <div class="course-card">

                    <div class="course-card-header">

                        <div class="course-icon">
                            📚
                        </div>

                        <span>
                            Course #${id}
                        </span>

                    </div>


                    <h3>
                        ${escapeHtml(name)}
                    </h3>


                    <p>
                        ${escapeHtml(description)}
                    </p>


                    <div class="course-card-actions">

                        <button
                            class="btn btn-secondary"
                            onclick="viewCourse(${id})">
                            View Details
                        </button>

                        ${
                            isStudent()
                            ? `
                                <button
                                    class="btn btn-success"
                                    onclick="enrollInCourse(${id})">
                                    Enroll
                                </button>
                            `
                            : ""
                        }

                    </div>

                </div>
            `;

        }).join("");

}


// ======================================================
// COURSE MODAL
// ======================================================

function openCourseModal(course = null) {

    if (!isAdmin()) {
        return;
    }


    const modal =
        document.getElementById(
            "courseModal"
        );

    const form =
        document.getElementById(
            "courseForm"
        );

    if (!modal || !form) {
        return;
    }


    form.reset();


    const idField =
        document.getElementById(
            "courseId"
        );

    const nameField =
        document.getElementById(
            "courseName"
        );

    const descriptionField =
        document.getElementById(
            "courseDescription"
        );


    if (course) {

        idField.value =
            course.id ??
            course.courseId ??
            "";

        nameField.value =
            course.name ??
            course.courseName ??
            "";

        descriptionField.value =
            course.description ??
            "";

    } else {

        idField.value = "";

    }


    modal.classList.remove(
        "hidden"
    );

}


// ======================================================
// CLOSE COURSE MODAL
// ======================================================

function closeCourseModal() {

    const modal =
        document.getElementById(
            "courseModal"
        );

    if (modal) {

        modal.classList.add(
            "hidden"
        );
    }

}


// ======================================================
// EDIT COURSE
// ======================================================

function editCourse(id) {

    if (!isAdmin()) {
        return;
    }


    const course =
        courses.find(function (item) {

            return Number(
                item.id ??
                item.courseId
            ) === Number(id);

        });


    if (!course) {

        showToast(
            "Course not found."
        );

        return;
    }


    openCourseModal(
        course
    );

}


// ======================================================
// COURSE FORM
// ======================================================

const courseForm =
    document.getElementById(
        "courseForm"
    );

if (courseForm) {

    courseForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            if (!isAdmin()) {

                showToast(
                    "Only administrators can manage courses."
                );

                return;
            }


            const id =
                document.getElementById(
                    "courseId"
                ).value;


            const name =
                document.getElementById(
                    "courseName"
                ).value.trim();


            const description =
                document.getElementById(
                    "courseDescription"
                ).value.trim();


            if (!name) {

                showToast(
                    "Course name is required."
                );

                return;
            }


            const payload = {
                name: name,
                description: description
            };


            try {

                let response;


                if (id) {

                    response =
                        await apiFetch(
                            `${API_BASE}/courses/${id}`,
                            {
                                method: "PUT",

                                body:
                                    JSON.stringify(
                                        payload
                                    )
                            }
                        );

                } else {

                    response =
                        await apiFetch(
                            `${API_BASE}/courses`,
                            {
                                method: "POST",

                                body:
                                    JSON.stringify(
                                        payload
                                    )
                            }
                        );
                }


                if (!response.ok) {

                    const result =
                        await readResponse(
                            response
                        );

                    throw new Error(
                        result?.message ||
                        result ||
                        "Course operation failed."
                    );
                }


                closeCourseModal();

                await loadCourses();


                showToast(
                    id
                        ? "Course updated successfully."
                        : "Course added successfully."
                );

            } catch (error) {

                console.error(
                    "Course save error:",
                    error
                );

                showToast(
                    error.message
                );
            }

        }
    );

}


// ======================================================
// DELETE COURSE
// ======================================================

async function deleteCourse(id) {

    if (!isAdmin()) {

        showToast(
            "Only administrators can delete courses."
        );

        return;
    }


    const confirmed =
        confirm(
            "Are you sure you want to delete this course?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE}/courses/${id}`,
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            const result =
                await readResponse(
                    response
                );

            throw new Error(
                result?.message ||
                result ||
                "Failed to delete course."
            );
        }


        await loadCourses();

        showToast(
            "Course deleted successfully."
        );

    } catch (error) {

        console.error(
            "Delete course error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// VIEW COURSE
// ======================================================

async function viewCourse(id) {

    try {

        const response =
            await apiFetch(
                `${API_BASE}/courses/${id}`
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load course details."
            );
        }


        const course =
            await readResponse(
                response
            );


        const details =
            document.getElementById(
                "courseDetails"
            );


        if (!details) {
            return;
        }


        const name =
            course.name ??
            course.courseName ??
            "N/A";

        const description =
            course.description ??
            "N/A";


        details.innerHTML = `

            <div class="detail-item">
                <span>Course ID</span>
                <strong>
                    ${course.id ?? course.courseId ?? "N/A"}
                </strong>
            </div>

            <div class="detail-item">
                <span>Course Name</span>
                <strong>
                    ${escapeHtml(name)}
                </strong>
            </div>

            <div class="detail-item">
                <span>Description</span>
                <p>
                    ${escapeHtml(description)}
                </p>
            </div>

        `;


        const modal =
            document.getElementById(
                "detailsModal"
            );


        if (modal) {

            modal.classList.remove(
                "hidden"
            );
        }

    } catch (error) {

        console.error(
            "View course error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// CLOSE DETAILS MODAL
// ======================================================

function closeDetailsModal() {

    const modal =
        document.getElementById(
            "detailsModal"
        );

    if (modal) {

        modal.classList.add(
            "hidden"
        );
    }

}


// ======================================================
// POPULATE COURSE SELECT
// ======================================================

function populateCourseSelect() {

    const select =
        document.getElementById(
            "enrollmentCourse"
        );

    if (!select) {
        return;
    }


    select.innerHTML = `
        <option value="">
            Select Course
        </option>
    `;


    courses.forEach(function (course) {

        const id =
            course.id ??
            course.courseId;

        const name =
            course.name ??
            course.courseName ??
            "Unnamed Course";


        select.innerHTML += `
            <option value="${id}">
                ${escapeHtml(name)}
            </option>
        `;

    });

}


// ======================================================
// ENROLL IN COURSE
// ======================================================

async function enrollInCourse(courseId) {

    if (!isStudent()) {

        showToast(
            "Only students can enroll in courses."
        );

        return;
    }


    const confirmed =
        confirm(
            "Do you want to enroll in this course?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const payload = {
            courseId:
                Number(courseId)
        };


        const response =
            await apiFetch(
                `${API_BASE}/enrollments`,
                {
                    method: "POST",

                    body:
                        JSON.stringify(
                            payload
                        )
                }
            );


        if (!response.ok) {

            const result =
                await readResponse(
                    response
                );

            throw new Error(
                result?.message ||
                result ||
                "Enrollment failed."
            );
        }


        await loadEnrollments();

        showToast(
            "Successfully enrolled in the course."
        );

    } catch (error) {

        console.error(
            "Enrollment error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// ENROLLMENTS
// ======================================================

async function loadEnrollments() {

    try {

        let url;


        if (isAdmin()) {

            url =
                `${API_BASE}/enrollments`;

        } else if (isStudent()) {

            const studentId =
                getUserId();


            if (!studentId) {

                console.warn(
                    "Student ID not found in JWT."
                );

                return;
            }


            url =
                `${API_BASE}/enrollments/student/${studentId}`;

        } else {

            return;
        }


        const response =
            await apiFetch(
                url
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load enrollments."
            );
        }


        const data =
            await readResponse(
                response
            );


        enrollments =
            Array.isArray(data)
                ? data
                : [];


        renderEnrollments(
            enrollments
        );


        const totalEnrollments =
            document.getElementById(
                "totalEnrollments"
            );


        if (totalEnrollments) {

            totalEnrollments.textContent =
                enrollments.length;
        }


        const allEnrollments =
            document.getElementById(
                "allEnrollments"
            );


        if (
            allEnrollments &&
            isAdmin()
        ) {

            allEnrollments.textContent =
                enrollments.length;
        }

    } catch (error) {

        console.error(
            "Load enrollments error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// RENDER ENROLLMENTS
// ======================================================

function renderEnrollments(list) {

    const tableBody =
        document.getElementById(
            "enrollmentTableBody"
        );


    if (!tableBody) {
        return;
    }


    if (!list.length) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    <div class="empty-state">
                        <div class="empty-state-icon">
                            📝
                        </div>
                        <h3>No enrollments found</h3>
                        <p>
                            No enrollment records are available.
                        </p>
                    </div>
                </td>
            </tr>
        `;

        return;
    }


    tableBody.innerHTML =
        list.map(function (enrollment) {

            const id =
                enrollment.id ??
                enrollment.enrollmentId;

            const studentId =
                enrollment.studentId ??
                enrollment.student?.id ??
                "N/A";

            const course =
                enrollment.course ??
                enrollment.courseName ??
                enrollment.course?.name ??
                "N/A";

            const courseName =
                typeof course === "object"
                    ? (
                        course.name ??
                        course.courseName ??
                        "N/A"
                    )
                    : course;


            const date =
                enrollment.enrollmentDate ??
                enrollment.date ??
                enrollment.createdAt ??
                "N/A";


            let actions = `
                <button
                    class="action-btn action-view"
                    onclick="viewEnrollment(${id})">
                    View
                </button>
            `;


            if (isAdmin()) {

                actions += `

                    <button
                        class="action-btn action-edit"
                        onclick="editEnrollment(${id})">
                        Edit
                    </button>

                    <button
                        class="action-btn action-delete"
                        onclick="deleteEnrollment(${id})">
                        Delete
                    </button>

                `;
            }


            return `
                <tr>

                    <td>${id}</td>

                    <td>${studentId}</td>

                    <td>
                        ${escapeHtml(
                            String(courseName)
                        )}
                    </td>

                    <td>
                        ${escapeHtml(
                            String(date)
                        )}
                    </td>

                    <td>
                        <div class="actions">
                            ${actions}
                        </div>
                    </td>

                </tr>
            `;

        }).join("");

}


// ======================================================
// ENROLLMENT MODAL
// ======================================================

function openEnrollmentModal(
    enrollment = null
) {

    if (!isAdmin()) {
        return;
    }


    const modal =
        document.getElementById(
            "enrollmentModal"
        );

    const form =
        document.getElementById(
            "enrollmentForm"
        );


    if (!modal || !form) {
        return;
    }


    form.reset();


    const id =
        document.getElementById(
            "enrollmentId"
        );

    const student =
        document.getElementById(
            "studentId"
        );

    const course =
        document.getElementById(
            "enrollmentCourse"
        );


    if (enrollment) {

        id.value =
            enrollment.id ??
            enrollment.enrollmentId ??
            "";

        student.value =
            enrollment.studentId ??
            enrollment.student?.id ??
            "";

        course.value =
            enrollment.courseId ??
            enrollment.course?.id ??
            "";

    } else {

        id.value = "";

    }


    modal.classList.remove(
        "hidden"
    );

}


// ======================================================
// CLOSE ENROLLMENT MODAL
// ======================================================

function closeEnrollmentModal() {

    const modal =
        document.getElementById(
            "enrollmentModal"
        );


    if (modal) {

        modal.classList.add(
            "hidden"
        );
    }

}


// ======================================================
// ENROLLMENT FORM
// ======================================================

const enrollmentForm =
    document.getElementById(
        "enrollmentForm"
    );


if (enrollmentForm) {

    enrollmentForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            if (!isAdmin()) {

                showToast(
                    "Only administrators can manage enrollments."
                );

                return;
            }


            const id =
                document.getElementById(
                    "enrollmentId"
                ).value;


            const studentId =
                document.getElementById(
                    "studentId"
                ).value;


            const courseId =
                document.getElementById(
                    "enrollmentCourse"
                ).value;


            if (!studentId || !courseId) {

                showToast(
                    "Student and course are required."
                );

                return;
            }


            const payload = {

                studentId:
                    Number(studentId),

                courseId:
                    Number(courseId)

            };


            try {

                let response;


                if (id) {

                    response =
                        await apiFetch(
                            `${API_BASE}/enrollments/${id}`,
                            {
                                method: "PUT",

                                body:
                                    JSON.stringify(
                                        payload
                                    )
                            }
                        );

                } else {

                    response =
                        await apiFetch(
                            `${API_BASE}/enrollments`,
                            {
                                method: "POST",

                                body:
                                    JSON.stringify(
                                        payload
                                    )
                            }
                        );
                }


                if (!response.ok) {

                    const result =
                        await readResponse(
                            response
                        );

                    throw new Error(
                        result?.message ||
                        result ||
                        "Enrollment operation failed."
                    );
                }


                closeEnrollmentModal();

                await loadEnrollments();


                showToast(
                    id
                        ? "Enrollment updated successfully."
                        : "Enrollment created successfully."
                );

            } catch (error) {

                console.error(
                    "Enrollment save error:",
                    error
                );

                showToast(
                    error.message
                );
            }

        }
    );

}


// ======================================================
// EDIT ENROLLMENT
// ======================================================

function editEnrollment(id) {

    if (!isAdmin()) {
        return;
    }


    const enrollment =
        enrollments.find(function (item) {

            return Number(
                item.id ??
                item.enrollmentId
            ) === Number(id);

        });


    if (!enrollment) {

        showToast(
            "Enrollment not found."
        );

        return;
    }


    openEnrollmentModal(
        enrollment
    );

}


// ======================================================
// DELETE ENROLLMENT
// ======================================================

async function deleteEnrollment(id) {

    if (!isAdmin()) {
        return;
    }


    const confirmed =
        confirm(
            "Are you sure you want to delete this enrollment?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE}/enrollments/${id}`,
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            const result =
                await readResponse(
                    response
                );

            throw new Error(
                result?.message ||
                result ||
                "Failed to delete enrollment."
            );
        }


        await loadEnrollments();


        showToast(
            "Enrollment deleted successfully."
        );

    } catch (error) {

        console.error(
            "Delete enrollment error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// VIEW ENROLLMENT
// ======================================================

function viewEnrollment(id) {

    const enrollment =
        enrollments.find(function (item) {

            return Number(
                item.id ??
                item.enrollmentId
            ) === Number(id);

        });


    if (!enrollment) {

        showToast(
            "Enrollment not found."
        );

        return;
    }


    const course =
        enrollment.course ??
        enrollment.courseName ??
        "N/A";


    const courseName =
        typeof course === "object"
            ? (
                course.name ??
                course.courseName ??
                "N/A"
            )
            : course;


    const details =
        document.getElementById(
            "courseDetails"
        );


    if (!details) {
        return;
    }


    details.innerHTML = `

        <div class="detail-item">
            <span>Enrollment ID</span>
            <strong>
                ${enrollment.id ??
                enrollment.enrollmentId ??
                "N/A"}
            </strong>
        </div>

        <div class="detail-item">
            <span>Student ID</span>
            <strong>
                ${enrollment.studentId ??
                enrollment.student?.id ??
                "N/A"}
            </strong>
        </div>

        <div class="detail-item">
            <span>Course</span>
            <strong>
                ${escapeHtml(
                    String(courseName)
                )}
            </strong>
        </div>

        <div class="detail-item">
            <span>Date</span>
            <strong>
                ${escapeHtml(
                    String(
                        enrollment.enrollmentDate ??
                        enrollment.date ??
                        "N/A"
                    )
                )}
            </strong>
        </div>

    `;


    const modal =
        document.getElementById(
            "detailsModal"
        );


    if (modal) {

        modal.classList.remove(
            "hidden"
        );
    }

}


// ======================================================
// MY COURSES
// ======================================================

async function loadMyCourses() {

    if (!isStudent()) {
        return;
    }


    const container =
        document.getElementById(
            "myCoursesGrid"
        );


    if (!container) {
        return;
    }


    const studentId =
        getUserId();


    if (!studentId) {

        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">
                    ⚠️
                </div>
                <h3>Student ID not found</h3>
                <p>
                    Your student ID could not be found in your login information.
                </p>
            </div>
        `;

        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE}/enrollments/student/${studentId}`
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load your courses."
            );
        }


        const data =
            await readResponse(
                response
            );


        const myEnrollments =
            Array.isArray(data)
                ? data
                : [];


        if (!myEnrollments.length) {

            container.innerHTML = `
                <div class="empty-state">

                    <div class="empty-state-icon">
                        📚
                    </div>

                    <h3>No courses yet</h3>

                    <p>
                        You are not enrolled in any courses.
                    </p>

                </div>
            `;

            return;
        }


        container.innerHTML =
            myEnrollments.map(function (item) {

                const course =
                    item.course ??
                    {};


                const courseId =
                    item.courseId ??
                    course.id ??
                    "";


                const courseName =
                    item.courseName ??
                    course.name ??
                    course.courseName ??
                    "Course";


                const description =
                    course.description ??
                    "No description available.";


                return `
                    <div class="course-card">

                        <div class="course-card-header">

                            <div class="course-icon">
                                📚
                            </div>

                            <span>
                                Enrollment
                            </span>

                        </div>


                        <h3>
                            ${escapeHtml(
                                String(courseName)
                            )}
                        </h3>


                        <p>
                            ${escapeHtml(
                                String(description)
                            )}
                        </p>


                        <div class="course-card-actions">

                            <button
                                class="btn btn-secondary"
                                onclick="viewCourse(${courseId})">
                                View Details
                            </button>

                        </div>

                    </div>
                `;

            }).join("");


    } catch (error) {

        console.error(
            "Load my courses error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// STUDENTS
// ======================================================

let students = [];


// ======================================================
// LOAD STUDENTS
// ======================================================

async function loadStudents() {

    if (!isAdmin()) {
        return;
    }


    const tableBody =
        document.getElementById(
            "studentTableBody"
        );


    if (!tableBody) {
        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE}/students`
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load students."
            );
        }


        const data =
            await readResponse(
                response
            );


        students =
            Array.isArray(data)
                ? data
                : [];


        renderStudents(
            students
        );


        const totalStudents =
            document.getElementById(
                "totalStudents"
            );


        if (totalStudents) {

            totalStudents.textContent =
                students.length;
        }


    } catch (error) {

        console.error(
            "Load students error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// RENDER STUDENTS
// ======================================================

function renderStudents(list) {

    const tableBody =
        document.getElementById(
            "studentTableBody"
        );


    if (!tableBody) {
        return;
    }


    if (!list.length) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="6">

                    <div class="empty-state">

                        <div class="empty-state-icon">
                            👨‍🎓
                        </div>

                        <h3>No students found</h3>

                        <p>
                            No students are currently registered.
                        </p>

                    </div>

                </td>
            </tr>
        `;

        return;
    }


    tableBody.innerHTML =
        list.map(function (student) {

            const id =
                student.id ??
                student.studentId;


            const name =
                student.name ??
                student.fullName ??
                "N/A";


            const email =
                student.email ??
                "N/A";


            const username =
                student.username ??
                "N/A";


            return `
                <tr>

                    <td>
                        ${id ?? "N/A"}
                    </td>

                    <td>
                        <strong>
                            ${escapeHtml(
                                String(name)
                            )}
                        </strong>
                    </td>

                    <td>
                        ${escapeHtml(
                            String(email)
                        )}
                    </td>

                    <td>
                        ${escapeHtml(
                            String(username)
                        )}
                    </td>

                    <td>
                        <span class="badge badge-student">
                            STUDENT
                        </span>
                    </td>

                    <td>

                        <div class="actions">

                            <button
                                class="action-btn action-view"
                                onclick="viewStudent(${id})">
                                View
                            </button>

                            <button
                                class="action-btn action-edit"
                                onclick="editStudent(${id})">
                                Edit
                            </button>

                            <button
                                class="action-btn action-delete"
                                onclick="deleteStudent(${id})">
                                Delete
                            </button>

                        </div>

                    </td>

                </tr>
            `;

        }).join("");

}


// ======================================================
// STUDENT SEARCH
// ======================================================

function filterStudents() {

    const input =
        document.getElementById(
            "studentSearch"
        );


    if (!input) {
        return;
    }


    const search =
        input.value
            .toLowerCase()
            .trim();


    const filtered =
        students.filter(function (student) {

            const id =
                String(
                    student.id ??
                    student.studentId ??
                    ""
                );


            const name =
                String(
                    student.name ??
                    student.fullName ??
                    ""
                );


            const email =
                String(
                    student.email ??
                    ""
                );


            const username =
                String(
                    student.username ??
                    ""
                );


            return (
                id.toLowerCase().includes(search) ||
                name.toLowerCase().includes(search) ||
                email.toLowerCase().includes(search) ||
                username.toLowerCase().includes(search)
            );

        });


    renderStudents(
        filtered
    );

}


// ======================================================
// STUDENT MODAL
// ======================================================

function openStudentModal(
    student = null
) {

    if (!isAdmin()) {
        return;
    }


    const modal =
        document.getElementById(
            "studentModal"
        );


    const form =
        document.getElementById(
            "studentForm"
        );


    if (!modal || !form) {
        return;
    }


    form.reset();


    const id =
        document.getElementById(
            "studentEditId"
        );


    const name =
        document.getElementById(
            "studentName"
        );


    const email =
        document.getElementById(
            "studentEmail"
        );


    const username =
        document.getElementById(
            "studentUsername"
        );


    const password =
        document.getElementById(
            "studentPassword"
        );


    if (student) {

        id.value =
            student.id ??
            student.studentId ??
            "";


        name.value =
            student.name ??
            student.fullName ??
            "";


        email.value =
            student.email ??
            "";


        username.value =
            student.username ??
            "";


        password.value = "";

    } else {

        id.value = "";

    }


    modal.classList.remove(
        "hidden"
    );

}


// ======================================================
// CLOSE STUDENT MODAL
// ======================================================

function closeStudentModal() {

    const modal =
        document.getElementById(
            "studentModal"
        );


    if (modal) {

        modal.classList.add(
            "hidden"
        );
    }

}


// ======================================================
// STUDENT FORM
// ======================================================

const studentForm =
    document.getElementById(
        "studentForm"
    );


if (studentForm) {

    studentForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            if (!isAdmin()) {

                showToast(
                    "Only administrators can manage students."
                );

                return;
            }


            const id =
                document.getElementById(
                    "studentEditId"
                ).value;


            const name =
                document.getElementById(
                    "studentName"
                ).value.trim();


            const email =
                document.getElementById(
                    "studentEmail"
                ).value.trim();


            const username =
                document.getElementById(
                    "studentUsername"
                ).value.trim();


            const password =
                document.getElementById(
                    "studentPassword"
                ).value;


            if (!name) {

                showToast(
                    "Student name is required."
                );

                return;
            }


            if (!email) {

                showToast(
                    "Student email is required."
                );

                return;
            }


            if (!username) {

                showToast(
                    "Username is required."
                );

                return;
            }


            const payload = {

                name: name,

                email: email,

                username: username

            };


            // Password only when supplied

            if (password) {

                payload.password =
                    password;
            }


            try {

                let response;


                if (id) {

                    response =
                        await apiFetch(
                            `${API_BASE}/students/${id}`,
                            {
                                method: "PUT",

                                body:
                                    JSON.stringify(
                                        payload
                                    )
                            }
                        );

                } else {

                    if (!password) {

                        showToast(
                            "Password is required for a new student."
                        );

                        return;
                    }


                    response =
                        await apiFetch(
                            `${API_BASE}/students`,
                            {
                                method: "POST",

                                body:
                                    JSON.stringify(
                                        payload
                                    )
                            }
                        );
                }


                if (!response.ok) {

                    const result =
                        await readResponse(
                            response
                        );


                    throw new Error(
                        result?.message ||
                        result ||
                        "Student operation failed."
                    );
                }


                closeStudentModal();

                await loadStudents();


                showToast(
                    id
                        ? "Student updated successfully."
                        : "Student added successfully."
                );


            } catch (error) {

                console.error(
                    "Student save error:",
                    error
                );

                showToast(
                    error.message
                );
            }

        }
    );

}


// ======================================================
// EDIT STUDENT
// ======================================================

async function editStudent(id) {

    if (!isAdmin()) {
        return;
    }


    let student =
        students.find(function (item) {

            return Number(
                item.id ??
                item.studentId
            ) === Number(id);

        });


    try {

        const response =
            await apiFetch(
                `${API_BASE}/students/${id}`
            );


        if (response.ok) {

            const data =
                await readResponse(
                    response
                );


            if (data) {
                student = data;
            }
        }

    } catch (error) {

        console.warn(
            "Could not fetch student details:",
            error
        );
    }


    if (!student) {

        showToast(
            "Student not found."
        );

        return;
    }


    openStudentModal(
        student
    );

}


// ======================================================
// VIEW STUDENT
// ======================================================

async function viewStudent(id) {

    if (!isAdmin()) {
        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE}/students/${id}`
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load student."
            );
        }


        const student =
            await readResponse(
                response
            );


        const details =
            document.getElementById(
                "courseDetails"
            );


        if (!details) {
            return;
        }


        details.innerHTML = `

            <div class="detail-item">
                <span>Student ID</span>
                <strong>
                    ${student.id ??
                    student.studentId ??
                    "N/A"}
                </strong>
            </div>

            <div class="detail-item">
                <span>Name</span>
                <strong>
                    ${escapeHtml(
                        String(
                            student.name ??
                            student.fullName ??
                            "N/A"
                        )
                    )}
                </strong>
            </div>

            <div class="detail-item">
                <span>Email</span>
                <strong>
                    ${escapeHtml(
                        String(
                            student.email ??
                            "N/A"
                        )
                    )}
                </strong>
            </div>

            <div class="detail-item">
                <span>Username</span>
                <strong>
                    ${escapeHtml(
                        String(
                            student.username ??
                            "N/A"
                        )
                    )}
                </strong>
            </div>

            <div class="detail-item">
                <span>Role</span>
                <strong>
                    STUDENT
                </strong>
            </div>

        `;


        const modal =
            document.getElementById(
                "detailsModal"
            );


        if (modal) {

            modal.classList.remove(
                "hidden"
            );
        }


    } catch (error) {

        console.error(
            "View student error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// DELETE STUDENT
// ======================================================

async function deleteStudent(id) {

    if (!isAdmin()) {
        return;
    }


    const confirmed =
        confirm(
            "Are you sure you want to delete this student?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE}/students/${id}`,
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            const result =
                await readResponse(
                    response
                );


            throw new Error(
                result?.message ||
                result ||
                "Failed to delete student."
            );
        }


        await loadStudents();


        showToast(
            "Student deleted successfully."
        );


    } catch (error) {

        console.error(
            "Delete student error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// PROFILE
// ======================================================

async function loadProfile() {

    if (!isStudent()) {
        return;
    }


    const studentId =
        getUserId();


    if (!studentId) {

        console.warn(
            "Student ID not found."
        );

        return;
    }


    try {

        let response =
            await apiFetch(
                `${API_BASE}/students/${studentId}`
            );


        if (!response.ok) {

            throw new Error(
                "Failed to load profile."
            );
        }


        const student =
            await readResponse(
                response
            );


        currentUser = {
            ...currentUser,
            ...student
        };


        const profileStudentId =
            document.getElementById(
                "profileStudentId"
            );


        const profileUsername =
            document.getElementById(
                "profileUsername"
            );


        const profileName =
            document.getElementById(
                "profileName"
            );


        const profileEmail =
            document.getElementById(
                "profileEmail"
            );


        const profileRole =
            document.getElementById(
                "profileRole"
            );


        if (profileStudentId) {

            profileStudentId.textContent =
                student.id ??
                student.studentId ??
                studentId;
        }


        if (profileUsername) {

            profileUsername.textContent =
                student.username ??
                getUsername();
        }


        if (profileName) {

            profileName.textContent =
                student.name ??
                student.fullName ??
                "N/A";
        }


        if (profileEmail) {

            profileEmail.textContent =
                student.email ??
                "N/A";
        }


        if (profileRole) {

            profileRole.textContent =
                "STUDENT";
        }


    } catch (error) {

        console.error(
            "Load profile error:",
            error
        );

        showToast(
            error.message
        );
    }

}


// ======================================================
// OPEN PROFILE MODAL
// ======================================================

function openProfileModal() {

    if (!isStudent()) {
        return;
    }


    const modal =
        document.getElementById(
            "profileModal"
        );


    const name =
        document.getElementById(
            "profileEditName"
        );


    const email =
        document.getElementById(
            "profileEditEmail"
        );


    const username =
        document.getElementById(
            "profileEditUsername"
        );


    if (!modal) {
        return;
    }


    if (name) {

        name.value =
            currentUser?.name ??
            currentUser?.fullName ??
            "";
    }


    if (email) {

        email.value =
            currentUser?.email ??
            "";
    }


    if (username) {

        username.value =
            currentUser?.username ??
            getUsername();
    }


    modal.classList.remove(
        "hidden"
    );

}


// ======================================================
// CLOSE PROFILE MODAL
// ======================================================

function closeProfileModal() {

    const modal =
        document.getElementById(
            "profileModal"
        );


    if (modal) {

        modal.classList.add(
            "hidden"
        );
    }

}


// ======================================================
// PROFILE FORM
// ======================================================

const profileForm =
    document.getElementById(
        "profileForm"
    );


if (profileForm) {

    profileForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            if (!isStudent()) {
                return;
            }


            const studentId =
                getUserId();


            if (!studentId) {

                showToast(
                    "Student ID not found."
                );

                return;
            }


            const name =
                document.getElementById(
                    "profileEditName"
                ).value.trim();


            const email =
                document.getElementById(
                    "profileEditEmail"
                ).value.trim();


            const username =
                document.getElementById(
                    "profileEditUsername"
                ).value.trim();


            if (!name || !email || !username) {

                showToast(
                    "All profile fields are required."
                );

                return;
            }


            const payload = {

                name: name,

                email: email,

                username: username

            };


            try {

                const response =
                    await apiFetch(
                        `${API_BASE}/students/${studentId}`,
                        {
                            method: "PUT",

                            body:
                                JSON.stringify(
                                    payload
                                )
                        }
                    );


                if (!response.ok) {

                    const result =
                        await readResponse(
                            response
                        );


                    throw new Error(
                        result?.message ||
                        result ||
                        "Failed to update profile."
                    );
                }


                closeProfileModal();

                await loadProfile();


                showToast(
                    "Profile updated successfully."
                );


            } catch (error) {

                console.error(
                    "Profile update error:",
                    error
                );

                showToast(
                    error.message
                );
            }

        }
    );

}


// ======================================================
// DASHBOARD
// ======================================================

async function loadDashboard() {

    try {

        await loadCourses();

        await loadEnrollments();

        if (isAdmin()) {

            await loadStudents();
        }

    } catch (error) {

        console.error(
            "Dashboard loading error:",
            error
        );
    }

}


// ======================================================
// HTML ESCAPE
// ======================================================

function escapeHtml(value) {

    if (value === null ||
        value === undefined) {

        return "";
    }


    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


// ======================================================
// CLOSE MODALS WHEN CLICKING OUTSIDE
// ======================================================

document.addEventListener(
    "click",
    function (event) {

        const modals =
            document.querySelectorAll(
                ".modal"
            );


        modals.forEach(function (modal) {

            if (
                event.target === modal
            ) {

                modal.classList.add(
                    "hidden"
                );
            }

        });

    }
);


// ======================================================
// ESC KEY CLOSE MODALS
// ======================================================

document.addEventListener(
    "keydown",
    function (event) {

        if (event.key !== "Escape") {
            return;
        }


        document.querySelectorAll(
            ".modal"
        ).forEach(function (modal) {

            modal.classList.add(
                "hidden"
            );

        });

    }
);

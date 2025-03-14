import CONFIG from "../Config.jsx";


const baseUrl = `/api/auth`;
const userBaseUrl = `/api/users`;

export async function fetchUser() {
    const url= `${baseUrl}/me`
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch user");
    }
    return response.json();
}

export async function loginUser(email, password) {
    const url= `${baseUrl}/login`
    const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
    });
    if (!response.ok) {
        throw new Error("Login failed");
    }
    return response.json();
}

export async function logoutUser() {
    const url = `${baseUrl}/logout`
    const response = await fetch(url, {
        method: "POST",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Logout failed");
    }
}

    export async function getAllUserEmails() {
        const url = `${userBaseUrl}/emails`;
        const response = await fetch(url, {
            method: "GET",
            credentials: "include",
        });
        if (!response.ok) {
            throw new Error("Failed to fetch user emails");
        }
        return response.json();
    }

    export async function getAllRoles() {
        const url = `${userBaseUrl}/roles`;
        const response = await fetch(url, {
            method: "GET",
            credentials: "include",
        });
        if (!response.ok) {
            throw new Error("Failed to fetch roles");
        }
        return response.json();
}

export async function createUser(data) {
    const response = await fetch(userBaseUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(data),
    });
    if (!response.ok) {
        throw new Error("Failed to create user");
    }
    return response.json();
}

export async function deleteUser(userId) {
    const url = `${userBaseUrl}/${userId}`;
    const response = await fetch(url, {
        method: "DELETE",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to delete user");
    }
}

export async function getAllUsersWithInstanceAdminRole() {
    const url = `${userBaseUrl}/instance-admins`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch users with InstanceAdmin role");
    }
    return response.json();
}

export async function getAllUsersWithProjectAdminRole() {
    const url = `${userBaseUrl}/project-admins`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch users with ProjectAdmin role");
    }
    return response.json();
}

export async function getUsersWithSystemAdminRole() {
    const url = `${userBaseUrl}/system-admins`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch users with SystemAdmin role");
    }
    return response.json();
}

export async function getUserProjectsByUserId(userId) {
    const url = `${userBaseUrl}/${userId}/user-projects`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch user projects");
    }
    return response.json();
}

export async function getUserInstancesByUserId(userId) {
    const url = `${userBaseUrl}/${userId}/user-instances`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch user instances");
    }
    return response.json();
}

export async function getProjectsForProjectAdmin(userId) {
    const url = `${userBaseUrl}/${userId}/admin-projects`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch projects for ProjectAdmin");
    }
    return response.json();
}

export async function getInstancesForInstanceAdmin(userId) {
    const url = `${userBaseUrl}/${userId}/admin-instances`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch instances for InstanceAdmin");
    }
    return response.json();
}

export async function updateUser(userId, userDTO) {
    const url = `${userBaseUrl}/${userId}`;
    const response = await fetch(url, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(userDTO),
    });

    if (!response.ok) {
        throw new Error("Failed to update user");
    }
    return response.json();
}
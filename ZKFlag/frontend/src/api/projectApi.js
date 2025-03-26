import { getResource} from "./resourceCrudApi.js";
import CONFIG from "../Config.jsx";

const baseUrl = `/api/projects`
export async function getProjects(){
    try {
        const response  = await fetch(baseUrl,{credentials: "include"})
        if (!response .ok){
            throw new Error("Could not fetch projects!");
        }
        const data = await response.json()
        return data
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}

export async function getProjectById(id){
    const data = await getProjects()
    const projectList = data.projects
    const project = projectList.find(el => el.id === id);
    if(!project){
        throw new Error("Could not find project!");
    }
    return project
}


export async function getAllConstraintsInToggle(projectId, toggleId) {
    const url = `${baseUrl}/${projectId}/toggles/${toggleId}/constraints`;
    try {
        const response = await getResource(url);
        return response;
    } catch (error) {
        console.error(`Failed to fetch constraints: ${error.message}`);
        throw error;
    }
}

export async function getProjectOverview(id){
    const url = `${baseUrl}/${id}`
    try {
        const responseData  = await getResource(url)
        return responseData
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}


export async function createProject(data){
    try {
        const response  = await fetch(baseUrl,
            { method: "post",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data) }
        )
        if (!response .ok){
            throw new Error("Could not create project!");
        }
        return true
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function updateProject(id, data){
    const url = `${baseUrl}/${id}`
    try {
        const response = await fetch(url,
            {
                method: "put",
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include",
                body: JSON.stringify(data) }
        )
        if (!response.ok) {
            throw {
                message: data.message,
                statusText: response.statusText,
                status: response.status
            }
        }
        return true
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function deleteProject(id) {
    const url = `${baseUrl}/${id}`
    try {
        const response = await fetch(url, {
            method: 'DELETE',
            credentials: "include"
        });
        if (!response.ok){
            throw new Error("Could not delete project!");
        }
        return true
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function removeAccessToProject(projectId, userId) {
    const url = `${baseUrl}/${projectId}/access/remove?userId=${userId}`;
    const response = await fetch(url, {
        method: "POST",
        credentials: "include"
    });
    if (!response.ok) {
        throw new Error("Failed to remove access to project");
    }
}

export async function addAccessToProject(projectId, projectUsersAddAccessDTO) {
    const url = `${baseUrl}/${projectId}/access`;
    const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(projectUsersAddAccessDTO),
    });
    if (!response.ok) {
        throw new Error("Failed to add access to project");
    }
}

export async function getUsersWithProjectAdminRole(projectId) {
    const url = `${baseUrl}/${projectId}/project-admins`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch users with ProjectAdmin role");
    }
    return response.json();
}
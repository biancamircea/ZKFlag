import {createResource, deleteResource, getResource} from "./resourceCrudApi.js";
import {toast} from "react-toastify";

const baseUrl = "http://localhost:8080/projects"
export async function getProjects(){
    try {
        const response  = await fetch(baseUrl)
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
    const project = projectList.find(el => el.id === Number(id));
    if(!project){
        throw new Error("Could not find project!");
    }
    return project
}

export async function getConstraintInToggle(projectId, toggleId, constraintId) {
    const url = `${baseUrl}/${projectId}/toggles/${toggleId}/constraints/${constraintId}`;
    try {
        const response = await getResource(url);
        return response; // presupunem că `getResource` returnează direct JSON-ul din răspuns
    } catch (error) {
        console.error(`Failed to fetch constraint: ${error.message}`);
        throw error;
    }
}

export async function getAllConstraintsInToggle(projectId, toggleId) {
    const url = `${baseUrl}/${projectId}/toggles/${toggleId}/constraints`;
    try {
        const response = await getResource(url);
        return response; // Returnează lista de constrângeri
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
            console.log(error)
            throw error
        }
    }
}

// export async function getProjectApiTokens(id){
//     const url = `${baseUrl}/${id}/api-tokens`
//     try {
//         const response  = await fetch(url)
//         if (!response.ok){
//             const message = await response.text()
//             throw {
//                 message: message,
//             }
//         }
//         const data = await response.json()
//         return data
//     } catch (error) {
//         if (error instanceof TypeError) {
//             throw new Error('Network error occurred. Please check your internet connection.')
//         } else {
//             throw error
//         }
//     }
// }

export async function createProject(data){
    try {
        const response  = await fetch(baseUrl,
            { method: "post",
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

// export async function toggleEnvironmentInProject(projectId, envId, change){
//     let url
//     if(change){
//         url = `${baseUrl}/${projectId}/environments/${envId}/on`
//     } else {
//         url = `${baseUrl}/${projectId}/environments/${envId}/off`
//     }
//     try {
//         const response  = await fetch(
//             url,
//             { method: "post"}
//         )
//         if (!response.ok){
//             throw new Error("Could not enable environment!");
//         }
//         return true
//     } catch (error) {
//         if (error instanceof TypeError) {
//             throw new Error('Network error occurred. Please check your internet connection.')
//         } else {
//             throw error
//         }
//     }
// }

// export async function createProjectApiToken(projectId, data){
//     const url = `${baseUrl}/${projectId}/api-tokens`
//     try {
//         const response = await createResource(url, data)
//         return response
//     } catch (error) {
//         if (error instanceof TypeError) {
//             throw new Error('Network error occurred. Please check your internet connection.')
//         } else {
//             return false
//         }
//     }
// }

export async function updateProject(id, data){
    const url = `${baseUrl}/${id}`
    try {
        const response = await fetch(url,
            {
                method: "put",
                headers: {
                    "Content-Type": "application/json"
                },
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
            method: 'DELETE'
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

// export async function deleteProjectApiToken(projectId, apiTokenId) {
//     const url = `${baseUrl}/${projectId}/api-tokens/${apiTokenId}`
//     try {
//         const response = await deleteResource(url)
//         return  response
//     } catch (error) {
//         if (error instanceof TypeError) {
//             throw new Error('Network error occurred. Please check your internet connection.')
//         } else {
//             return false
//         }
//     }
// }
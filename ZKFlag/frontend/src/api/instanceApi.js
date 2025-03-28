import {getResource} from "./resourceCrudApi.js"
import CONFIG from "../Config.jsx";


export async function getAllEnvironmentsFromInstance(instanceId) {
    const url = `/api/instances/${instanceId}/environments`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch environments: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

export async function getToggleEnvironments(instanceId, toggleId) {
    const url = `/api/instances/${instanceId}/toggles/${toggleId}/environments`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch toggle environments: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

export async function getToggleEnvironments2(instanceId, envId) {
    const url = `/api/instances/${instanceId}/environments/${envId}/toggles`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch toggle environments: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

export async function createInstance(projectId, instanceData) {
    const url = `/api/projects/${projectId}/instances`;
    try {
        const response = await fetch(url, {
            method: 'POST',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(instanceData),
        });

        if (!response.ok) {
            throw new Error(`Failed to create instance: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

export async function deleteInstance(projectId, instanceId) {
    const url = `/api/projects/${projectId}/instances/${instanceId}`;
    try {
        const response = await fetch(url, {
            method: 'DELETE',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to delete instance: ${response.statusText}`);
        }else{
            return true
        }
    } catch (error) {
        throw error;
    }
}


export async function getInstanceApiTokens(instanceId) {
    const url = `/api/instances/${instanceId}/api-tokens`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch API tokens: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}


export async function toggleEnvironmentInInstance(instanceId, envId, enable) {
    const action = enable ? "on" : "off";
    const url = `/api/instances/${instanceId}/environments/${envId}/${action}`;

    try {
        const response = await fetch(url, {
            method: 'POST',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to ${action} environment: ${response.statusText}`);
        }else {return true};
    } catch (error) {
        throw error;
    }
}


export async function createInstanceApiToken(projectId,instanceId, apiTokenData) {
    const url = `/api/projects/${projectId}/instances/${instanceId}/api-tokens`;
    try {
        const response = await fetch(url, {
            method: 'POST',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(apiTokenData),
        });

        if (!response.ok) {
            throw new Error(`Failed to create API token: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

export async function deleteInstanceApiToken(instanceId, tokenId) {
    const url = `/api/instances/${instanceId}/api-tokens/${tokenId}`;
    try {
        const response = await fetch(url, {
            method: 'DELETE',
            credentials: "include",
        });

        if (!response.ok) {
            throw new Error(`Failed to delete API token: ${response.statusText}`);
        }
    } catch (error) {
        throw error;
    }
}

export async function getInstanceOverview(id){
    const url = `/api/instances/${id}`
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

export async function getAllInstancesFromProject(projectId) {
    const url = `/api/projects/${projectId}/instances`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch instances: ${response.statusText}`);
        }
        return await response.json();
    } catch (error) {
        throw error;
    }
}

export async function addAccessToInstance(instanceId, instanceUsersAddAccessDTO) {
    const url = `/api/instances/${instanceId}/access`;
    const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(instanceUsersAddAccessDTO),
    });
    if (!response.ok) {
        throw new Error("Failed to add access to instance");
    }
}

export async function removeAccessToInstance(instanceId, userId) {
    console.log("userId",userId)
    const url = `/api/instances/${instanceId}/access/remove?userId=${userId}`;
    const response = await fetch(url, {
        method: "POST",
        credentials: "include",
    });

    if (!response.ok) {
        throw new Error("Failed to remove access to instance");
    }
}


export async function getUsersWithInstanceAdminRole(instanceId) {
    const url = `/api/instances/${instanceId}/instance-admins`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch users with InstanceAdmin role");
    }
    return response.json();
}

export async function getProjectForInstance(instanceId) {
    const url = `/api/instances/${instanceId}/project`;
    const response = await fetch(url, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch project for instance");
    }
    return response.json();
}

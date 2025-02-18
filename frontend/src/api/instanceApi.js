import {getResource} from "./resourceCrudApi.js";
import {toast} from "react-toastify";

const baseUrl = 'http://localhost:8080';

export async function getAllEnvironmentsFromInstance(instanceId) {
    const url = `${baseUrl}/instances/${instanceId}/environments`;
    try {
        const response = await fetch(url, {
            method: 'GET',
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
    const url = `${baseUrl}/instances/${instanceId}/toggles/${toggleId}/environments`;
    try {
        const response = await fetch(url, {
            method: 'GET',
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
    const url = `${baseUrl}/projects/${projectId}/instances`;
    console.log( JSON.stringify(instanceData));
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(instanceData),
        });
        console.log("Creating instance2")
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
    const url = `${baseUrl}/projects/${projectId}/instances/${instanceId}`;
    try {
        const response = await fetch(url, {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error(`Failed to delete instance: ${response.statusText}`);
        }else{
            return true
        }
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}


export async function getInstanceApiTokens(instanceId) {
    const url = `${baseUrl}/instances/${instanceId}/api-tokens`;
    try {
        const response = await fetch(url, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch API tokens: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

export async function enableInstanceEnvironment(instanceId, envId) {
    const url = `${baseUrl}/instances/${instanceId}/environments/${envId}/on`;
    try {
        const response = await fetch(url, {
            method: 'POST',
        });

        if (!response.ok) {
            throw new Error(`Failed to enable environment: ${response.statusText}`);
        }
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}


export async function toggleEnvironmentInInstance(instanceId, envId, enable) {
    const action = enable ? "on" : "off";
    const url = `${baseUrl}/instances/${instanceId}/environments/${envId}/${action}`;

    try {
        const response = await fetch(url, {
            method: 'POST',
        });

        if (!response.ok) {
            throw new Error(`Failed to ${action} environment: ${response.statusText}`);
        }else {return true};
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}


export async function createInstanceApiToken(projectId,instanceId, apiTokenData) {
   console.log("intra aici1");
    const url = `${baseUrl}/projects/${projectId}/instances/${instanceId}/api-tokens`;
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(apiTokenData),
        });
        console.log("intra aici2");
        if (!response.ok) {
            throw new Error(`Failed to create API token: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

export async function deleteInstanceApiToken(instanceId, tokenId) {
    const url = `${baseUrl}/instances/${instanceId}/api-tokens/${tokenId}`;
    try {
        const response = await fetch(url, {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error(`Failed to delete API token: ${response.statusText}`);
        }
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

export async function getInstanceOverview(id){
    const url = `${baseUrl}/instances/${id}`
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

export async function getAllInstancesFromProject(projectId) {
    const url = `${baseUrl}/projects/${projectId}/instances`;
    try {
        const response = await fetch(url, {
            method: 'GET',
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch instances: ${response.statusText}`);
        }
        return await response.json();
    } catch (error) {
        console.error(error.message);
        throw error;
    }
}

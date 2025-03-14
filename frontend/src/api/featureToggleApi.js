import {createResource, deleteResource, getResource, updateResource} from "./resourceCrudApi.js";
import CONFIG from "../Config.jsx";

const projectBaseUrl = `/api/projects`
const toggleBaseUrl = `/api/toggles`

export async function isToggleEnabled(toggleId, envId, instanceId) {
    const url = `${toggleBaseUrl}/${toggleId}/environments/${envId}/instances/${instanceId}/enabled`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch toggle enabled status: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

export async function getTogglesFromProject(projectId){
    const url =  `${projectBaseUrl}/${projectId}/toggles`
    try {
        const response  = await fetch(url,{credentials: "include"})
        if (!response .ok){
            throw new Error("Could not fetch toggles!");
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


export async function getToggleFromProject(projectId, toggleId){
    const url =  `${projectBaseUrl}/${projectId}/toggles/${toggleId}`
    try {
        const response  = await fetch(url,{credentials: "include"})
        if (!response .ok){
            throw new Error("Could not fetch toggle!");
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

export async function createToggle(projectId, data){
    const url = `${projectBaseUrl}/${projectId}/toggles`
    try {
        const response  = await fetch(url,
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

export async function addTagToToggle(projectId, toggleId, tagId){
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/tags/${tagId}`
    try {
        const response  = await fetch(url,
        {
                method: "post",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                }
            }
        )
        if (!response.ok){
            const message = await response.text()
            throw {
                message: message,
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

export async function addConstraintInToggleEnv(projectId, toggleId, data) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/constraints`;
    try {
        const response = await createResource(url, data);

        return response;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            return false;
        }
    }
}


export async function addPayloadInToggleEnv(projectId, toggleId, instanceId, environmentId, data) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/instances/${instanceId}/environments/${environmentId}/payload`;
    try {
        const response = await createResource(url, data);
        return response;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            return false;
        }
    }
}

export async function toggleFeature(projectId, toggleId, instanceId, envName, change) {
    let url;
    if (change) {
        url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/instances/${instanceId}/environments/${envName}/on`;
    } else {
        url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/instances/${instanceId}/environments/${envName}/off`;
    }
    try {
        const response = await fetch(url, { method: "post" ,credentials: "include"});
        if (!response.ok) {
            throw new Error("Could not toggle feature!");
        }
        return true;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error("Network error occurred. Please check your internet connection.");
        } else {
            throw error;
        }
    }
}


export async function updateToggleFromProject(projectId, toggleId, data){
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}`
    try {
        const response = await fetch(url,
            {
                method: "put",
                credentials: "include",
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

export async function updateConstraintInToggleEnv(projectId, toggleId, constraintId, data) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/constraints/${constraintId}`;

    try {
        const responseData = await updateResource(url, data);
        return responseData;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            return false;
        }
    }
}


export async function updatePayloadInToggleEnv(projectId, toggleId, instanceId, environmentId, data) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/instances/${instanceId}/environments/${environmentId}/payload`;
    try {
        const response = await updateResource(url, data);
        return response;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            return false;
        }
    }
}


export async function deleteToggleFromProject(projectId, toggleId) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}`
    try {
        const response = await deleteResource(url)
        return  response
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}
export async function removeTagFromToggle(projectId, toggleId, tagId) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/tags/${tagId}`
    try {
        const response = await deleteResource(url)
        return  response
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function deleteConstraintFromToggleEnv(projectId, toggleId, constraintId) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/constraints/${constraintId}`;
    try {
        const response = await deleteResource(url);
        return response;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            return false;
        }
    }
}

export async function deleteAllConstraintsFromToggleEnv(projectId, toggleId) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/constraints`;
    try {
        const response = await deleteResource(url);
        return response;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            return false;
        }
    }
}


export async function deletePayloadFromToggleEnv(projectId, toggleId, environmentId, instanceId) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/instances/${instanceId}/environments/${environmentId}/payload`;
    try {
        const response = await deleteResource(url);
        return response;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            return false;
        }
    }
}

export async function setToggleSchedule(toggleId, environmentId, instanceId, scheduleData) {
    const url = `${toggleBaseUrl}/${toggleId}/instances/${instanceId}/environments/${environmentId}/schedule`;
    try {
        const response = await fetch(url, {
            method: 'PUT',
            credentials: "include",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(scheduleData),
        });

        if (!response.ok) {
            throw new Error(`Failed to save schedule: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}


export async function getAllStrategiesForFlag(toggleId, instanceId) {
    const url = `${toggleBaseUrl}/${toggleId}/instances/${instanceId}/schedule_strategies`;
    try {
        const response = await getResource(url);
        return response;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            throw new Error(`Failed to fetch strategies: ${error.message}`);
        }
    }
}

export async function getStrategyForEnvironment(toggleId, environmentId, instanceId) {
    try {
        const strategies = await getAllStrategiesForFlag(toggleId, instanceId);

        const environmentStrategy = strategies.find(
            (strategy) => strategy.environmentId === environmentId
        );
        if (!environmentStrategy) {
            return null;
        }

        return environmentStrategy;
    } catch (error) {
        throw new Error(`Failed to fetch strategy for environment: ${error.message}`);
    }
}

export async function getAllConstraintsForInstanceEnvironment(projectId,toggleId, instanceId, environmentId) {
    const url = `${toggleBaseUrl}/${toggleId}/instances/${instanceId}/environment/${environmentId}/constraints`;
    try {
        const response = await getResource(url);
        return response;
    } catch (error) {
        throw error;
    }
}

export async function getConstraintFromToggle(projectId, toggleId, constraintId) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/constraints/${constraintId}`;
    try {
        const response = await getResource(url);
        return response;
    } catch (error) {
        throw error;
    }
}

export async function getConstraintFromToggleEnvironment(toggleId, instanceId, environmentId, constraintId) {
    const url = `${toggleBaseUrl}/${toggleId}/instances/${instanceId}/environment/${environmentId}/constraints/${constraintId}`;
    try {
        const response = await getResource(url);
        return response;
    } catch (error) {
        throw error;
    }
}

export async function addConstraintInToggle(projectId, toggleId, data) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/constraints`;
    try {
        const response = await createResource(url, data);
        return response;
    } catch (error) {
        throw error;
    }
}

export async function updateConstraintValuesForToggleEnvironment(projectId, toggleId, instanceId, environmentId, constraintId, newValues) {
    const url = `${projectBaseUrl}/${projectId}/toggles/${toggleId}/environment/${environmentId}/instances/${instanceId}/constraints/${constraintId}/values`;
    try {
        const response = await updateResource(url, newValues);
        return response;
    } catch (error) {
        throw error;
    }
}

export async function getConstraintValues(constraintId) {
    const url = `/api/constraints/${constraintId}/values`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
        });

        const data = await response.json()
        return data
    } catch (error) {
        throw error;
    }
}

export async function getToggleEnvironment( toggleId, instanceId, environmentId) {
    const url = `${toggleBaseUrl}/${toggleId}/instances/${instanceId}/environments/${environmentId}`;
    try {
        const response = await fetch(url, {
            method: 'GET',
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch toggle environment: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

export async function uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch(`/api/minio/upload`, {
            method: 'POST',
            body: formData,
            credentials: "include",
        });

        if (response.ok) {
            return await response.text();
        } else {
            throw new Error('File upload failed');
        }
    } catch (error) {
        throw error;
    }
}

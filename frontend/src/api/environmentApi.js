import {toast} from "react-toastify";
const baseUrl = "http://localhost:8080/environments"
export async function getEnvironments(id){
    const url = id ? `${baseUrl}/${id}` : `${baseUrl}`
    try {
        const response  = await fetch(url)
        if (!response .ok){
            throw new Error("Could not fetch environments!");
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

export async function getActiveEnvironmentsForInstance(instanceId) {
    const url = `${baseUrl}/active/${instanceId}`;
    try {
        console.log(` Fetching active environments for instance ID: ${instanceId}`);

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error("Could not fetch active environments!");
        }

        const data = await response.json();
        console.log("API Response:", data); // Debugging

        return data;
    } catch (error) {
        console.error("API Fetch Error:", error);
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            throw error;
        }
    }
}

//export async function getEnabledEnvFromProject(projectId){}
export async function getEnabledEnvFromInstance(instanceId) {
    const url = `${baseUrl}/instances/${instanceId}`;
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error("Could not fetch environments for the instance!");
        }
        const data = await response.json(); // EntityModel conține InstanceEnvironmentsResponseDTO
        return data.instanceEnvironmentDTOList; // Accesăm direct lista de DTO-uri
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            throw error;
        }
    }
}


export async function createEnvironment(envData){
    // console.log(JSON.stringify(envData))
    const res = await fetch(baseUrl,
        { method: "post",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(envData) }
    )
    const data = await res.json()

    if (!res.ok) {
        throw {
            message: data.message,
            statusText: res.statusText,
            status: res.status
        }
    }
    return true
}

export async function toggleEnvironment(id, change){
    let url = ""
    if(change){
        url = `${baseUrl}/${id}/on`
    } else {
        url = `${baseUrl}/${id}/off`
    }
    try {
        const response  = await fetch(
            url,
            { method: "post"}
        )
        if (!response.ok){
            throw new Error("Could not fetch environments!");
        }
        return true
    } catch (error) {
        if (error instanceof TypeError) {
            return false
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}

export async function updateEnvironment(id, envData){
    const url = `${baseUrl}/${id}`
    const res = await fetch(url,
        {
            method: "put",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(envData) }
    )
    const data = await res.json()

    if (!res.ok) {
        throw {
            message: data.message,
            statusText: res.statusText,
            status: res.status
        }
    }
    return true
}

export async function deleteEnvironment(id) {
    const res = await fetch(`${baseUrl}/${id}`, {
        method: 'DELETE'
    });
    if (!res.ok) {
        throw {
            error: 'Problem deleting environment'
        };
    }
    return res
}
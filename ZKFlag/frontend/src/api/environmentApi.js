import CONFIG from '../Config'
const baseUrl = `/api/environments`

export async function getEnvironments(id){
    const url = id ? `${baseUrl}/${id}` : `${baseUrl}`
    try {
        const response  = await fetch(url,{credentials: "include"})
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

        const response = await fetch(url,{credentials: "include"});
        if (!response.ok) {
            throw new Error("Could not fetch active environments!");
        }

        const data = await response.json();

        return data;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            throw error;
        }
    }
}

export async function getEnabledEnvFromInstance(instanceId) {
    const url = `${baseUrl}/instances/${instanceId}`;
    try {
        const response = await fetch(url,{credentials: "include"});
        if (!response.ok) {
            throw new Error("Could not fetch environments for the instance!");
        }
        const data = await response.json();
        return data.instanceEnvironmentDTOList;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.');
        } else {
            throw error;
        }
    }
}


export async function createEnvironment(envData){
    const res = await fetch(baseUrl,
        { method: "post",
            credentials: "include",
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
            { method: "post",
            credentials: "include"
            }
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
            credentials: "include",
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
        method: 'DELETE',
        credentials: "include"
    });
    if (!res.ok) {
        throw {
            error: 'Problem deleting environment'
        };
    }
    return res
}
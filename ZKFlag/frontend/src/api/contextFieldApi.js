import CONFIG from '../Config'

const projectBaseUrl = `/api/projects`

export async function getContextFields(projectId){
    console.log("projectId in context field api: "+projectId)
    const url =  `${projectBaseUrl}/${projectId}/context-fields`
    try {
        const response  = await fetch(url,{credentials: "include"})
        if (!response.ok){
            const message = await response.text()
            throw {
                message: message,
            }
        }
        const data = await response.json()
        console.log("data in context field api: ",data)
        return data
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}

export async function getContextField(projectId, contextFieldId){
    const url =  `${projectBaseUrl}/${projectId}/context-fields/${contextFieldId}`
    try {
        const response  = await fetch(url,{credentials: "include"})
        if (!response.ok){
            const message = await response.text()
            throw {
                message: message,
            }
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

export async function createContextField(projectId, data){
    const url =  `${projectBaseUrl}/${projectId}/context-fields`
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
            throw new Error("Could not create context field!");
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

export async function updateContextField(projectId, contextFieldId, data){
    const url =  `${projectBaseUrl}/${projectId}/context-fields/${contextFieldId}`
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

export async function deleteContextField(projectId, contextFieldId) {
    const url =  `${projectBaseUrl}/${projectId}/context-fields/${contextFieldId}`
    try {
        const response = await fetch(url, {
            method: 'DELETE',
            credentials: "include"
        });
        if (!response.ok){
            throw new Error("Could not delete context field!");
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
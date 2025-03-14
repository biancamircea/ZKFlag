export async function getResource(url){
    const response  = await fetch(url,{credentials: "include"})
    if (!response.ok){
        const message = await response.text()
        throw {
            message: message,
        }
    }
    const data = await response.json()
    return data
}

export async function createResource(url, data){
    const response  = await fetch(url,
        { method: "post",
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
    const responseData = await response.json()
    return responseData
}

export async function updateResource(url, data){
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
    const responseData = await response.json()
    return responseData
}

export async function deleteResource(url) {
    const response = await fetch(url, {
        method: 'DELETE',
        credentials: "include"
    });
    if (!response.ok){
        const message = await response.text()
        throw {
            message: message,
        }
    }
    return true
}
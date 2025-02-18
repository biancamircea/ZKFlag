import {deleteResource, getResource, updateResource} from "./resourceCrudApi.js";

const baseUrl = "http://localhost:8080/applications"
export async function getApplications(){
    try {
        const responseData = getResource(baseUrl)
        return responseData
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}

export async function getApplicationDetails(appId){
    const url = `${baseUrl}/${appId}`
    try {
        const responseData = getResource(url)
        return responseData
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}

export async function updateApplication(appId, data){
    const url = `${baseUrl}/${appId}`
    try {
        const responseData = updateResource(url, data)
        return responseData
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function deleteApplication(appId) {
    const url = `${baseUrl}/${appId}`
    try {
        const response = deleteResource(url)
        return response
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function deleteApplicationInstances(appId) {
    const url = `${baseUrl}/${appId}/instances`
    try {
        const response = deleteResource(url)
        return response
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function deleteApplicationInstance(appId, instanceId) {
    const url = `${baseUrl}/${appId}/instances/${instanceId}`
    try {
        const response = deleteResource(url)
        return response
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}
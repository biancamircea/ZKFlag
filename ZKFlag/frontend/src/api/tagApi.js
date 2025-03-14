import {createResource, deleteResource, getResource, updateResource} from "./resourceCrudApi.js";
import CONFIG from "../Config.jsx";

const projectBaseUrl = `/api/projects`

export async function getTags(projectId){
    const url =  `${projectBaseUrl}/${projectId}/tags`
    try {
        const reponseData = await getResource(url)
        return reponseData
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}

export async function getTag(projectId, tagId){
    const url =  `${projectBaseUrl}/${projectId}/tags/${tagId}`
    try {
        const reponseData = await getResource(url)
        return reponseData
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            throw error
        }
    }
}


export async function createTag(projectId, data){
    const url =  `${projectBaseUrl}/${projectId}/tags`
    try {
        const response = await createResource(url, data)
        return true
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function updateTag(projectId, tagId, data){
    const url =  `${projectBaseUrl}/${projectId}/tags/${tagId}`
    try {
        const response = updateResource(url, data)
        return true
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error('Network error occurred. Please check your internet connection.')
        } else {
            return false
        }
    }
}

export async function deleteTag(projectId, tagId) {
    const url =  `${projectBaseUrl}/${projectId}/tags/${tagId}`
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
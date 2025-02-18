import {getResource} from "./resourceCrudApi.js";

const baseUrl = "http://localhost:8080/events"
const queryUrl = "http://localhost:8080/events?"

export async function getEvents(project, featureId){
    const url = featureId
        ? queryUrl + new URLSearchParams({toggleId: featureId})
        : (project ? queryUrl + new URLSearchParams({project: project}) : baseUrl)
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
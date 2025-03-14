import {getResource} from "./resourceCrudApi.js";
import CONFIG  from "../Config.jsx";

const baseUrl = `/api/events/all`;

export async function getEvents(projectId = null, instanceId = null) {
    const params = new URLSearchParams();

    if (projectId) params.append("projectId", projectId);
    if (instanceId) params.append("instanceId", instanceId);

    const url = params.toString() ? `${baseUrl}?${params.toString()}` : baseUrl;

    try {
        const responseData = await getResource(url);
        return responseData;
    } catch (error) {
        if (error instanceof TypeError) {
            throw new Error("Network error occurred. Please check your internet connection.");
        } else {
            throw error;
        }
    }
}


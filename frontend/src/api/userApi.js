import axios from "axios";

const baseUrl = "http://localhost:8080/users";

export async function authenticateUser(email, password) {
    try {
        const url = `${baseUrl}/authenticate`;
        const response = await axios.post(
            url,
            { email, password },
            {
                headers: { "Content-Type": "application/json" },
                withCredentials: true
            }
        );
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 401) {
            throw new Error("Authentication failed");
        } else {
            throw new Error("An error occurred during authentication");
        }
    }
}

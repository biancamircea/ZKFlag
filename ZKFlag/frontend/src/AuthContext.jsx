import { createContext, useContext, useState, useEffect } from "react";
import { fetchUser, loginUser, logoutUser } from "./api/userApi";

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchUser()
            .then((data) => {
                if (data.email) {
                    setUser(data);
                }
            })
            .catch(() => setUser(null))
            .finally(() => setLoading(false));
    }, []);

    const login = async (email, password) => {
        try {
            const data = await loginUser(email, password);
            setUser(data);
            return data;
        } catch (error) {
            throw new Error("Login failed");
        }
    };

    const logout = async () => {
        try {
            await logoutUser();
            setUser(null);
            localStorage.removeItem("jwt");

        } catch (error) {
            throw new Error("Logout failed");
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

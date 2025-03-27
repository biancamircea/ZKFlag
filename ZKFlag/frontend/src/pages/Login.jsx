import React, { useState } from "react";
import { useAuth } from "../AuthContext.jsx";
import { useNavigate } from "react-router-dom";

function Login() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();
        if (!email || !password) {
            setError("Both email and password are required");
            return;
        }
        setError("");

        try {
            const userData = await login(email, password);

            if (!userData || !userData.role) {
                throw new Error("This role is not available");
            }

            switch (userData.role) {
                case "SystemAdmin":
                    navigate("/system-admin");
                    break;
                case "ProjectAdmin":
                    navigate("/projects");
                    break;
                case "InstanceAdmin":
                    navigate("/instances");
                    break;
                default:
                    navigate("/");
            }
        } catch (error) {
            setError("Authentication failed. Check your credentials.");
        }
    };


    return (
            <div className="login-container">
                <div>
                    <h1 className="project-title">Welcome to ZKFlag</h1>
                </div>
                <div className="login-box">
                    <h2 className="login-title">Login</h2>

                    {error && <p className="error-message">{error}</p>}

                    <form className="login-form" onSubmit={handleLogin}>
                        <label className="login-label">Email</label>
                        <input
                            type="email"
                            className="login-input"
                            placeholder="Enter your email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />

                        <label className="login-label">Password</label>
                        <input
                            type="password"
                            className="login-input"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />

                        <button type="submit" className="login-button">Login</button>
                    </form>
                </div>
            </div>
    );
}

export default Login;

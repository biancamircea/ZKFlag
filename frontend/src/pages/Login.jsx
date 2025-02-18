import React, { useState } from "react";
import { authenticateUser } from "../api/userApi";

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();
        if (!username || !password) {
            setError("Username și parola sunt necesare!");
            return;
        }
        setError(""); // Resetează eroarea dacă există date valide
        setSuccess(""); // Resetează mesajul de succes

        try {
            const response = await authenticateUser(username, password);
            setSuccess("Autentificare reușită!");
        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <div className="login-container">
            <div className="login-box">
                <h2 className="login-title">Login</h2>

                {error && <p className="error-message">{error}</p>}
                {success && <p className="success-message">{success}</p>}

                <form className="login-form" onSubmit={handleLogin}>
                    <input
                        type="text"
                        className="login-input"
                        placeholder="Enter your username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
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
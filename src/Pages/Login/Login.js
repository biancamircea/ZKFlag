import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../Components/Router/Router";
import "./Login.css";

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const auth = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");

        if (email.trim() !== "" && password.trim() !== "") {
            try {
                const response = await axios.post('/api/v1/auth/login', {
                    email: email,
                    password,
                });

                const { token, role,id } = response.data;
                localStorage.setItem('token', token);
                localStorage.setItem('role', role);
                localStorage.setItem('idUser',id);

                auth.login(role);

                if (role === 'ROLE_ADMIN') {
                    navigate("/pgAdmin");
                } else {
                    navigate("/home");
                }
            } catch (err) {
                setError("Incorrect password or email!");
            }
        } else {
        }
    };


    return (
        <div className="totLogin">
        <h1 className="title-login">Vacation manager</h1>
            <div className="login-container">
                <h1 className="login">Login</h1>
                <form onSubmit={handleSubmit}>
                    <div className="login2-div">
                        <label className="login2">Email:</label>
                        <input
                            type="text"
                            className="input-field"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>
                    <div className="login2-div">
                        <label className="login2">Password:</label>
                        <input
                            type="password"
                            className="input-field"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    <button type="submit" className="submit">Login</button>
                    {error && <p style={{ color: 'red', fontSize:'25px' }}>{error}</p>}
                </form>
            </div>
        </div>
    );
};

export default Login;

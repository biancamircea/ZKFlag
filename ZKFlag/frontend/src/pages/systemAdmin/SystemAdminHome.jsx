import React, { useState, useEffect } from 'react';
import Navbar from '../../components/nav/ApplicationNav.jsx';
import { getAllRoles,getAllUserEmails,createUser } from "../../api/userApi.js";
import {toast} from "react-toastify";

function SystemAdminHome() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('');
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [emails, setEmails] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchRoles = async () => {
            try {
                const rolesData = await getAllRoles();
                setRoles(rolesData);
                if (rolesData.length > 0) {
                    setRole(rolesData[0]);
                }
            } catch (error) {
                console.error('Failed to fetch roles:', error);
            } finally {
                setLoading(false);
            }
        };

        const fetchEmails = async () => {
            try {
                const emailsData = await getAllUserEmails(); // Așteaptă răspunsul corect
                setEmails(emailsData);
            } catch (error) {
                console.error('Failed to fetch emails:', error);
            } finally {
                setLoading(false);
            }
        }

        fetchRoles();
        fetchEmails()
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();

        if(!name || !email || !password || !role){
            setError("Please fill in all fields.");
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(email)) {
            setError("Please enter a valid email address.");
            return;
        }

        if (emails.includes(email)) {
            setError("This email is already in use. Please choose another one.");
            return;
        }

        const response = createUser({ email, name,role,password });
        if (response) {
           toast.success("User created.");
        }

        setEmail('');
        setName('');
        setPassword('');
        setRole(roles[0]);

    };

    return (
        <div className="layout-container">
            <br />
            <div className="create-form-container">
                <h2>Create New User</h2>
                <form onSubmit={handleSubmit} className="user-form">
                    <div className="create-form-field-item">
                        <label htmlFor="name">Name</label>
                        <input className="login-input"
                            type="text"
                            id="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>
                    <div className="create-form-field-item">
                        <label htmlFor="email">Email</label>
                        <input className="login-input"
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="create-form-field-item">
                        <label htmlFor="password">Password</label>
                        <input className="login-input"
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <div className="create-form-field-item">
                        <label htmlFor="role">Role</label>
                        <select
                            id="role"
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                            required
                            className="form-dropdown"
                            disabled={loading}
                        >
                            {loading ? (
                                <option>Loading roles...</option>
                            ) : (
                                roles.map((r) => (
                                    <option key={r} value={r}>
                                        {r}
                                    </option>
                                ))
                            )}
                        </select>
                    </div>
                    {error && <p className="error-message">{error}</p>}
                    <div className="create-form-buttons">
                        <button type="submit" className="submit-button">
                            Create User
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default SystemAdminHome;

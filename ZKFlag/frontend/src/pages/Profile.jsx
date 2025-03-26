import React, { useState, useEffect } from 'react';
import {fetchUser, updateUser, getAllUserEmails} from '../api/userApi';

import { toast } from 'react-toastify';

function Profile() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [user, setUser] = useState(null);
    const [emails, setEmails] = useState([]);

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const userData = await fetchUser();
                setUser(userData);
                setName(userData.name || '');
                setEmail(userData.email || '');
            } catch (error) {
                console.error('Failed to fetch user profile:', error);
            } finally {
                setLoading(false);
            }
        };

        const fetchEmails = async () => {
            try {
                const emailsData = await getAllUserEmails();
                setEmails(emailsData);
            } catch (error) {
                console.error('Failed to fetch emails:', error);
            } finally {
                setLoading(false);
            }
        }

        fetchEmails()
        fetchUserProfile();
    }, []);


    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        const updatedFields = {};
        if (name !== user.name) updatedFields.name = name;
        if (email !== user.email) updatedFields.email = email;
        if (password) updatedFields.password = password;

        if(updatedFields.email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                setError('Please enter a valid email address.');
                return;
            }
            if (emails.includes(email)) {
                setError('This email is already in use. Please choose another one.');
                return;
            }
        }

        if (Object.keys(updatedFields).length === 0) {
            setError('No changes were made.');
            return;
        }


        try {
            await updateUser(user.id, updatedFields);
            toast.success('Profile updated successfully!');
            setPassword('');
            window.location.reload()
        } catch (error) {
            console.error('Failed to update profile:', error);
            setError('Update failed. Please try again.');
        }
    };

    return (
        <div className="layout-container">
        <div className="create-form-container" style={{ width: '500px' }}>
            <h2>Edit Profile</h2>
            <form onSubmit={handleSubmit} className="user-form">
                <div className="create-form-field-item">
                    <label htmlFor="name">Name</label>
                    <input
                        type="text"
                        id="name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                        className="login-input"
                    />
                </div>
                <div className="create-form-field-item">
                    <label htmlFor="email">Email</label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        className="login-input"
                    />
                </div>
                <div className="create-form-field-item">
                    <label htmlFor="password">New Password (optional)</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="login-input"
                    />
                </div>
                {error && <p className="error-message">{error}</p>}
                <div className="create-form-buttons">
                    <button type="submit" className="submit-button" disabled={loading}>
                        Save Changes
                    </button>
                </div>
            </form>
        </div>
    </div>
    );
}

export default Profile;

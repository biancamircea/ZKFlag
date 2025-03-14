import React, { useEffect, useState } from "react";
import axios from "axios";
import './UserManagement.css';
import { useNavigate } from "react-router-dom";
import "../../index.css"

const UserManagement = () => {
    const [users, setUsers] = useState([]);
    const [departments, setDepartments] = useState([]);
    const [error, setError] = useState("");
    const [isDarkMode, setIsDarkMode] = useState(false);
    const [borderColor, setBorderColor]=useState([]);
    const [textColor, setTextColor]=useState([])

    const navigate = useNavigate();

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await axios.get("/api/v1/employees/");
                setUsers(response.data);
            } catch (err) {
                console.error("Error fetching users:", err);
            }
        };

        const fetchDepartments = async () => {
            try {
                const response = await axios.get("/api/v1/departaments/");
                setDepartments(response.data);
            } catch (err) {
                console.error("Error fetching departments:", err);
            }
        };

        fetchUsers();
        fetchDepartments();

        // Set dark mode based on localStorage value
        const darkMode = localStorage.getItem("isDark");
        setIsDarkMode(darkMode);
        setBorderColor( isDarkMode ? "white" : "black");
         setTextColor(isDarkMode ? "white" : "black");
    }, []);

    const handleDeleteUser = async (userId) => {
        try {
            await axios.delete(`/api/v1/employees/${userId}`);
            setUsers(users.filter(user => user.employeeId !== userId));
        } catch (error) {
            setError("Cannot delete user. They may be a supervisor or have active requests.");
            console.error("Error deleting user:", error);
        }
    };



    return (
        <div>
            <div className="user-management">
                <table className="user-table" style={{color:isDarkMode? "white" : "black", borderColor, borderStyle: "solid", borderWidth: "1px" }}>
                    <thead style={{color:isDarkMode? "white" : "black", borderColor, borderStyle: "solid", borderWidth: "1px" }}>
                    <tr>
                        <th style={{ color:isDarkMode? "white" : "black" }}>Last Name</th>
                        <th style={{ color:isDarkMode? "white" : "black" }}>First Name</th>
                        <th style={{ color:isDarkMode? "white" : "black"}}>Supervisor</th>
                        <th style={{color:isDarkMode? "white" : "black" }}>Department</th>
                        <th style={{ color:isDarkMode? "white" : "black" }}>Actions</th>
                    </tr>
                    </thead>
                    <tbody style={{ color: isDarkMode? "white" : "black",borderColor, borderStyle: "solid", borderWidth: "1px" }}>
                    {users.map(user => {
                        const supervisorName = user.supervisor ? `${user.supervisor.firstName} ${user.supervisor.lastName}` : "No Supervisor";
                        const department = departments.find(dep => dep.supervisor?.employeeId === user.supervisor?.employeeId ||
                            dep.supervisor?.employeeId === user.employeeId) || { departamentName: "No Department" };

                        return (
                            <tr key={user.employeeId} style={{ color:isDarkMode? "white" : "black",borderColor, borderStyle: "solid", borderWidth: "1px" }}>
                                <td style={{ color:isDarkMode? "white" : "black" }}>{user.lastName}</td>
                                <td style={{ color:isDarkMode? "white" : "black" }}>{user.firstName}</td>
                                <td style={{ color:isDarkMode? "white" : "black" }}>{supervisorName}</td>
                                <td style={{ color:isDarkMode? "white" : "black" }}>{department.departamentName}</td>
                                <td style={{ color:isDarkMode? "white" : "black" }}>
                                    <button className="btn_delete"
                                            onClick={() => handleDeleteUser(user.employeeId)}>Delete
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
            {error && <p className="error-message">{error}</p>}
            <button onClick={() => navigate('/create_user')} className="create-user-button">
                Create User
            </button>
            <button onClick={() => navigate('/create_dep')} className="create-user-button3">
                Create Department
            </button>
        </div>
    );
};

export default UserManagement;

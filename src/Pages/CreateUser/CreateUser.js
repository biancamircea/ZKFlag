import React, { useEffect, useState } from "react";
import axios from "axios";
import './CreateUser.css';
import { useNavigate } from "react-router-dom";

const CreateUser = () => {
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("ROLE_EMPLOYEE");
    const [departments, setDepartments] = useState([]);
    const [selectedDepartment, setSelectedDepartment] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDepartments = async () => {
            try {
                const response = await axios.get("/api/v1/departaments/");
                setDepartments(response.data);
            } catch (err) {
                console.error("Error fetching departments:", err);
            }
        };

        fetchDepartments();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!/^[a-zA-Z]+$/.test(name)) {
            setError("Invalid name");
            return;
        }
        if (!/^[a-zA-Z]+$/.test(surname)) {
            setError("Invalid surname");
            return;
        }
        if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
            setError("Invalid email format");
            return;
        }
        if (!/^(0[0-9]{9})$/.test(phoneNumber)) {
            setError("Invalid phone number");
            return;
        }

        const supervisorId = getSupervisorId(selectedDepartment);

        const newEmployee = {
            firstName: name,
            lastName: surname,
            email,
            phoneNumber,
            password,
            role,
            supervisor: { employeeId: supervisorId },
        };

        try {
            await axios.post("/api/v1/employees/", newEmployee);
            navigate("/pgAdmin");
        } catch (error) {
            console.error("Error creating user:", error);
            setError("Failed to create user");
        }
    };

    const getSupervisorId = (departmentId) => {


        const department = departments.find(dep => dep.departamentId === Number(departmentId));

        if (department) {
            return department.supervisor ? department.supervisor.employeeId : null;
        } else {
            console.warn("No department found for ID:", departmentId);
            return null;
        }
    };


    return (
        <div className="create-user-container">
            <h2>Create User</h2>
            {error && <p className="error-message">{error}</p>}
            <form className="create-user-form" onSubmit={handleSubmit}>
                <label className="form-label" style={{fontSize:"15px", margin:"5px",fontWeight:"normal"}}>
                    Name:
                    <input className="form-input" type="text" value={name} onChange={(e) => setName(e.target.value)} required />
                </label>
                <label className="form-label"  style={{fontSize:"15px", margin:"5px",fontWeight:"normal"}}>
                    Surname:
                    <input className="form-input" type="text" value={surname} onChange={(e) => setSurname(e.target.value)} required />
                </label>
                <label className="form-label"  style={{fontSize:"15px", margin:"5px",fontWeight:"normal"}}>
                    Email:
                    <input className="form-input" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </label>
                <label className="form-label"  style={{fontSize:"15px", margin:"5px",fontWeight:"normal"}}>
                    Phone Number:
                    <input className="form-input" type="text" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} required />
                </label>
                <label className="form-label"  style={{fontSize:"15px", margin:"5px",fontWeight:"normal"}}>
                    Password:
                    <input className="form-input" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </label>
                <label className="form-label"  style={{fontSize:"15px", margin:"5px",fontWeight:"normal"}}>
                    Role:
                    <select className="form-select" value={role} onChange={(e) => setRole(e.target.value)}>
                        <option value="ROLE_ADMIN" style={{fontSize:"15px"}}>ROLE_ADMIN</option>
                        <option value="ROLE_EMPLOYEE" style={{fontSize:"15px"}}>ROLE_EMPLOYEE</option>
                    </select>
                </label>
                <label className="form-label" style={{fontSize:"15px", margin:"5px",fontWeight:"normal"}}>
                    Department:
                    <select className="form-select" value={selectedDepartment}
                            onChange={(e) => {
                                setSelectedDepartment(e.target.value);
                            }} required>
                        <option value="">Select Department</option>
                        {departments.map(department => (
                            <option key={department.departamentId} value={department.departamentId}>
                                {department.departamentName}
                            </option>
                        ))}
                    </select>
                </label>
                <button className="create-user-button2" type="submit">Create User</button>
            </form>
        </div>
    );
};

export default CreateUser;

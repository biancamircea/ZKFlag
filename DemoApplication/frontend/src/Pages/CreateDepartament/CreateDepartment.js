import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./CreateDepartment.css"

const CreateDepartment = () => {
    const [departamentName, setDepartmentName] = useState("");
    const [supervisors, setSupervisors] = useState([]);
    const [selectedSupervisor, setSelectedSupervisor] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const [departments, setDepartments] = useState([]);

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

    useEffect(() => {
        const fetchSupervisors = async () => {
            try {
                const response = await axios.get("/api/v1/employees/");
                const allEmployees = response.data;

                if (departments.length > 0) {
                    const filteredSupervisors = allEmployees.filter(employee => {
                        return (
                            employee.role === "ROLE_EMPLOYEE" &&
                            employee.supervisorId !== -1 &&
                            !departments.some(dep => dep.supervisor.employeeId === employee.employeeId)
                        );
                    });

                    setSupervisors(filteredSupervisors);
                }
            } catch (err) {
                console.error("Error fetching employees:", err);
                setError("Failed to load employees.");
            }
        };

        fetchSupervisors();
    }, [departments]);


    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        const newDepartment = {
            departamentName,
            supervisor: {employeeId: selectedSupervisor}
        };

        try {
            await axios.post("/api/v1/departaments/", newDepartment);
            navigate("/pgAdmin");
        } catch (error) {
            console.error("Error creating department:", error);
            setError("Failed to create department.");
        }
    };

    return (
            <div className="create-department-container">
                <h2 className="scris_create_dep">Create Department</h2>
                {error && <p className="error-message">{error}</p>}
                <form className="create-department-form" onSubmit={handleSubmit}>
                    <label className="form-label">
                        Department Name:
                        <input
                            className="form-input"
                            type="text"
                            value={departamentName}
                            onChange={(e) => setDepartmentName(e.target.value)}
                            required
                        />
                    </label>
                    <label className="form-label">
                        Supervisor:
                        <select
                            className="form-select"
                            value={selectedSupervisor}
                            onChange={(e) => setSelectedSupervisor(e.target.value)}
                            required
                        >
                            <option value="">Select Supervisor</option>
                            {supervisors.map(supervisor => (
                                <option key={supervisor.employeeId} value={supervisor.employeeId}>
                                    {supervisor.firstName} {supervisor.lastName}
                                </option>
                            ))}
                        </select>
                    </label>
                    <button className="create-department-button" type="submit">Create Department</button>
                </form>
            </div>
    );
};

    export default CreateDepartment;

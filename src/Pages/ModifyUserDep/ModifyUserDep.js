import React, { useState, useEffect } from "react";
import axios from "axios";
import "./ModifyUserDep.css"

const ModifyUserDep = () => {
    const [employees, setEmployees] = useState([]);
    const [departments, setDepartments] = useState([]);
    const [selectedEmployee, setSelectedEmployee] = useState("");
    const [selectedDepartment, setSelectedDepartment] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

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
        const fetchEmployees = async () => {
            try {
                const response = await axios.get("/api/v1/employees/");
                const validEmployees = response.data.filter(employee =>
                    employee.role === "ROLE_EMPLOYEE" &&
                    employee.supervisorId !== -1 &&
                    !departments.some(dep => dep.supervisor?.employeeId === employee.employeeId)
                );
                setEmployees(validEmployees);
            } catch (err) {
                setError("Eroare la încărcarea angajaților.");
            }
        };

        fetchEmployees();
    }, [departments]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put(
                `/api/v1/employees/${selectedEmployee}/change-department`,
                JSON.stringify(selectedDepartment),
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );
            setSuccess("Mutarea a fost realizată cu succes.");
        } catch (error) {
            setError("Eroare la schimbarea departamentului.");
        }
    };


    return (
        <div className="modify-user-dep-container">
            <h2 className="scris">Change employee's department</h2>
            {error && <p className="error-message">{error}</p>}
            {success && <p className="success-message">{success}</p>}
            <form onSubmit={handleSubmit}>
                <label className="scris">
                    Choose employee:
                    <select
                        className="label_mod_userdep"
                        value={selectedEmployee}
                        onChange={(e) => setSelectedEmployee(e.target.value)}
                        required
                    >
                        <option className="scris" value="">Choose employee</option>
                        {employees.map(employee => (
                            <option key={employee.employeeId} value={employee.employeeId}>
                                {employee.firstName} {employee.lastName}
                            </option>
                        ))}
                    </select>
                </label>
                <label className="scris">
                   Choose the new department:
                    <select className="label_mod_userdep" value={selectedDepartment} onChange={(e) => setSelectedDepartment(e.target.value)} required>
                        <option  value="" >Choose department</option>
                        {departments.map(department => (
                            <option key={department.id} value={department.supervisor.employeeId}>
                                {department.departamentName}
                            </option>
                        ))}
                    </select>
                </label>
                <button className="buton_user" type="submit">Change department</button>
            </form>
        </div>
    );
};

export default ModifyUserDep;

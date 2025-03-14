import React, { useState, useEffect } from "react";
import axios from "axios";
import "./ModifyHeadDep.css";
import {useNavigate} from "react-router-dom";

const ModifyHeadDep = () => {
    const [departments, setDepartments] = useState([]);
    const [departamentId, setDepartmentId] = useState("");
    const [employees, setEmployees] = useState([]);
    const [newHeadId, setNewHeadId] = useState("");
    const navigate=useNavigate()

    useEffect(() => {
        axios.get("/api/v1/departaments/")
            .then(response => {
                setDepartments(response.data);
            })
            .catch(error => console.error("Error fetching departments:", error));
    }, []);

    useEffect(() => {
        if (departamentId) {
            axios.get(`/api/v1/departaments/${departamentId}`)
                .then(response => {
                    const supervisorId = response.data.supervisor.employeeId;


                    axios.get(`/api/v1/employees/by-supervisor/${supervisorId}`)
                        .then(response => {
                            setEmployees(response.data);
                        })
                        .catch(error => console.error("Error fetching employees:", error));
                })
                .catch(error => console.error("Error fetching department:", error));
        } else {
            setEmployees([]);
        }
    }, [departamentId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            console.log(newHeadId);
            await axios.put(`/api/v1/departaments/${departamentId}/updateHead`, null, {
                params: { newHeadId }
            });
            alert("Department head updated successfully.");
            navigate("/pgAdmin")

        } catch (error) {
            alert("Error updating department head: " + error.response?.data || error.message);
        }
    };

    return (
        <div className="modify-head-dep">
            <h2  style={{marginBottom:"40px",marginTop:"30px", fontWeight:"bold"}}>Modify Department Head</h2>
            <form onSubmit={handleSubmit}>
                <label htmlFor="departmentId">Select Department:</label>
                <select
                    className="label_mod_userdep"
                    id="departamentId"
                    value={departamentId}
                    onChange={(e) => setDepartmentId(e.target.value)}
                    required
                    style={{fontSize:"20px",backgroundColor:"white",border:"white",width:"80%"}}
                >
                    <option value="" >Select a department</option>
                    {departments.map(dept => (
                        <option className="options-head" key={dept.departamentId} value={dept.departamentId}>
                            {dept.departamentName}
                        </option>
                    ))}
                </select>

                {employees.length > 0 && (
                    <>
                        <label htmlFor="newHeadId"  >Select new department head:</label>
                        <select
                            className="label_mod_userdep"
                            id="newHeadId"
                            value={newHeadId}
                            onChange={(e) => setNewHeadId(e.target.value)}
                            required
                            style={{fontSize:"20px",backgroundColor:"white",border:"white",width:"80%"}}
                        >
                            <option  value="">Select an employee</option>
                            {employees.map(emp => (
                                <option key={emp.employeeId} value={emp.employeeId}>
                                    {emp.firstName} {emp.lastName}
                                </option>
                            ))}
                        </select>
                    </>
                )}

                <button className="btn-update-head" style={{color:"white", width:"80%", marginTop:"30px"}} type="submit" disabled={!departamentId || !newHeadId}>
                    Update Head
                </button>
            </form>
        </div>
    );
};

export default ModifyHeadDep;

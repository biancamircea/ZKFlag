import React, { useState, useEffect } from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import "./ModifyCommander.css"

const ModifyCommander = () => {
    const [departmentHeads, setDepartmentHeads] = useState([]);
    const [newCommanderId, setNewCommanderId] = useState("");
    const navigate=useNavigate()

    // Fetch current department heads
    useEffect(() => {
        const fetchDepartmentHeads = async () => {
            try {
                const response = await axios.get("/api/v1/departaments/heads");
                setDepartmentHeads(response.data);
            } catch (error) {
                console.error("Error fetching department heads:", error);
            }
        };
        fetchDepartmentHeads();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put("/api/v1/employees/update-commander", null, {
                params: { newCommanderId }
            });
            alert("Commander updated successfully.");
            navigate("/pgAdmin");
        } catch (error) {
            alert("Error updating commander: " + (error.response?.data || error.message));
        }
    };

    return (
        <div className="modify-commander">
            <h2 style={{color:"black",fontSize:"40px"}}>Modify Commander</h2>
            <form onSubmit={handleSubmit}>
                <label style={{color:"black",alignContent:"center",fontWeight:"normal",fontSize:"20px",textAlign:"left",marginLeft:"0px"}} htmlFor="newCommanderId">Select new commander:</label>
                <select
                    id="newCommanderId"
                    value={newCommanderId}
                    onChange={(e) => setNewCommanderId(e.target.value)}
                    required
                    style={{color:"black",alignContent:"center", marginLeft:"0px"}}
                >
                    <option value=""  style={{color:"black"}}>Select a commander</option>
                    {departmentHeads.map(head => (
                        <option key={head.employeeId} value={head.employeeId}>
                            {head.firstName} {head.lastName}
                        </option>
                    ))}
                </select>
                <button className="update-button" type="submit" style={{alignContent:"center",marginLeft:"0px"}}>Update Commander</button>
            </form>
        </div>
    );
};

export default ModifyCommander;

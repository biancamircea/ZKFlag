import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import './CreateRequest.css';


const CreateRequest = () => {
    const [dates, setDates] = useState({ startDate: null, endDate: null });
    const [reason, setReason] = useState("");
    const [employeeId, setEmployeeId] = useState(null);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const storedEmployeeId = localStorage.getItem("idUser");
        if (storedEmployeeId) {
            setEmployeeId(storedEmployeeId);
        }
    }, []);

    const handleCancel = () => {
        navigate("/home");
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");

        if (!dates.startDate || !dates.endDate || !reason) {
            setError("All fields are required.");
            return;
        }

        if (dates.endDate < dates.startDate) {
            setError("End date must be after start date.");
            return;
        }

        const requestData = {
            startDate: dates.startDate.toISOString().split('T')[0],
            endDate: dates.endDate.toISOString().split('T')[0],
            reason,
            employee: {
                employeeId: employeeId,
            },
        };

        try {
            const response = await axios.post("/api/v1/submitted/", requestData);
            navigate("/home");
        } catch (error) {
            console.error("Error submitting request:", error);

            if (error.response && error.response.status === 400 && error.response.data.message.includes("overlaps")) {
                setError("Conflict in vacation period. Please choose different dates.");
            } else {
                setError("The request could not be sent due to overlapping data. Please try again with other data.");
            }
        }
    };

    return (
            <div className="request-form">
                <h2>Create Request</h2>
                {error && <p className="error-message">{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="subTitluri">Choose the dates:</label>
                        <div className="date-picker-container">
                            <DatePicker
                                selected={dates.startDate}
                                onChange={(date) => setDates(prevDates => ({ ...prevDates, startDate: date }))}
                                selectsStart
                                startDate={dates.startDate}
                                endDate={dates.endDate}
                                placeholderText="Start Date"
                                dateFormat="yyyy-MM-dd"
                                className="date-picker"
                                minDate={new Date()}
                            />
                            <span className="scris">to</span>
                            <DatePicker
                                selected={dates.endDate}
                                onChange={(date) => setDates(prevDates => ({ ...prevDates, endDate: date }))}
                                selectsEnd
                                startDate={dates.startDate}
                                endDate={dates.endDate}
                                placeholderText="End Date"
                                dateFormat="yyyy-MM-dd"
                                className="date-picker"
                                minDate={dates.startDate || new Date()}
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="subTitluri" htmlFor="reason">Reason:</label>
                        <textarea
                            id="reason"
                            className="form-control"
                            value={reason}
                            onChange={(e) => setReason(e.target.value)}
                            required
                        />
                    </div>

                    <div className="button-group-create">
                        <button type="submit">OK</button>
                        <button type="button" className="cancel-btn" onClick={handleCancel}>Cancel</button>
                    </div>
                </form>
            </div>
    );
};

export default CreateRequest;

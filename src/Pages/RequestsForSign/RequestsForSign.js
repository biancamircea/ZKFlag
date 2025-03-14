import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import './RequestsForSign.css';
import Navbar from "../../Components/Navbar/Navbar";

const RequestsForSign = () => {
    const [requests, setRequests] = useState([]);
    const [error, setError] = useState(null);
    const employeeId = localStorage.getItem("idUser");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRequests = async () => {
            try {
                const response = await axios.get(`/api/v1/submitted/requests-for-sign`, {
                    params: { employeeId }
                });
                setRequests(response.data);
            } catch (err) {
                setError("You don’t have rights to sign requests.");
            }
        };

        fetchRequests();
    }, [employeeId]);

    const handleRequestClick = (requestId) => {
        navigate(`/request_for_sign/${requestId}`);
    };

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="request-container">
            <h2 className="request-title">Requests for Signature</h2>
            {requests.length > 0 ? (
                <ul className="request-list">
                    {requests.map(request => (
                        <li
                            key={request.requestId}
                            className="request-item"
                            onClick={() => handleRequestClick(request.requestId)}
                        >
                            <p className="request-details1">Employee: {request.employee.lastName} {request.employee.firstName}</p>
                            <p className="request-details1">Start Date: {request.startDate}</p>
                            <p className="request-details1">End Date: {request.endDate}</p>
                            <p className="request-details1">Reason: {request.reason}</p>
                        </li>
                    ))}
                </ul>
            ) : (
                <p className="no_req">No requests available for signing.</p>
            )}
        </div>
    );
};

export default RequestsForSign;

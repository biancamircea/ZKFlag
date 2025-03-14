import React, { useEffect, useState } from "react";
import {useNavigate, useParams} from "react-router-dom";
import axios from "axios";
import "./RequestForSign.css";

const RequestForSign = () => {
    const { requestId } = useParams();
    const [request, setRequest] = useState(null);
    const [error, setError] = useState(null);
    const [declineReason, setDeclineReason] = useState("");
    const [isDeclining, setIsDeclining] = useState(false);
    const navigate =useNavigate();

    useEffect(() => {
        const fetchRequestDetails = async () => {
            try {
                const response = await axios.get(`/api/v1/submitted/${requestId}`);
                setRequest(response.data);
            } catch (err) {
                setError("Failed to fetch request details.");
            }
        };

        fetchRequestDetails();
    }, [requestId]);

    const handleAccept = async () => {
        try {
            await axios.post("/api/v1/approved/", null, {
                params: { idSubmitted: requestId }
            });
            alert("Request approved successfully.");
            navigate("/requests_for_sign");
        } catch (err) {
            alert("Failed to approve the request.");
        }
    };

    const handleDecline = async () => {
        try {
            await axios.post("/api/v1/rejected/", {
                submittedRequest: { requestId: requestId },
                reason: declineReason || null
            });
            alert("Request declined successfully.");
            navigate("/requests_for_sign");
        } catch (err) {
            alert("Failed to decline the request.");
        }
    };

    if (error) {
        return <div>{error}</div>;
    }

    if (!request) {
        return <div>Loading...</div>;
    }

    return (
        <div className="request-details">
            <h2 className="request-title">Request Details</h2>
            <p className="request-info">Employee: {request.employee.lastName} {request.employee.firstName}</p>
            <p className="request-info">Start Date: {request.startDate}</p>
            <p className="request-info">End Date: {request.endDate}</p>
            <p className="request-info">Reason: {request.reason}</p>

            {isDeclining && (
                <div>
                    <label className="reason-label">Reason for Decline (optional):</label>
                    <input
                        type="text"
                        value={declineReason}
                        onChange={(e) => setDeclineReason(e.target.value)}
                        className="reason-input"
                    />
                </div>
            )}

            <div className="button-group1">
                <button onClick={handleAccept} className="button button-accept">Accept</button>
                <button onClick={() => setIsDeclining(!isDeclining)} className="button button-decline">Decline</button>
                {isDeclining && (
                    <button onClick={handleDecline} className="button button-confirm">Confirm Decline</button>
                )}
            </div>
        </div>
    );
};

export default RequestForSign;

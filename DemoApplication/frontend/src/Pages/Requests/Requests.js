import React, { useEffect, useState } from "react";
import axios from "axios";
import "./Requests.css";

const Requests = () => {
    const [requests, setRequests] = useState([]);
    const [approvedRequests, setApprovedRequests] = useState([]);
    const [rejectedRequests, setRejectedRequests] = useState([]);
    const [error, setError] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("pending");
    const employeeId = localStorage.getItem("idUser");

    useEffect(() => {
        const fetchRequests = async () => {
            try {
                const response = await axios.get(`/api/v1/submitted/employee/${employeeId}`);
                setRequests(response.data);
            } catch (err) {
                setError("Could not fetch pending requests.");
                console.error(err);
            }
        };

        const fetchApprovedRequests = async () => {
            try {
                const response = await axios.post(`/api/v1/approved/employee/${employeeId}`);
                setApprovedRequests(response.data);
            } catch (err) {
                setError("Could not fetch approved requests.");
                console.error(err);
            }
        };

        const fetchRejectedRequests = async () => {
            try {
                const response = await axios.post(`/api/v1/rejected/employee/${employeeId}`);
                setRejectedRequests(response.data);
            } catch (err) {
                setError("Could not fetch rejected requests.");
                console.error(err);
            }
        };

        fetchRequests();
        fetchApprovedRequests();
        fetchRejectedRequests();
    }, [employeeId]);

    const filteredRequests = requests.filter((request) => {
        const isApproved = approvedRequests.some(approvedRequest =>
            approvedRequest.submittedRequest.requestId === request.requestId
        );
        const isRejected = rejectedRequests.some(rejectedRequest =>
            rejectedRequest.submittedRequest.requestId === request.requestId
        );
        return !isApproved && !isRejected;
    });

    const handleCategoryChange = (event) => {
        setSelectedCategory(event.target.value);
    };

    return (
        <div>
            <div className="category-dropdown">
                <label htmlFor="category" style={{fontWeight:"bold"}}>Select Request Type: </label>
                <select id="category" value={selectedCategory} onChange={handleCategoryChange}>
                    <option value="pending">Pending Requests</option>
                    <option value="approved">Approved Requests</option>
                    <option value="rejected">Rejected Requests</option>
                </select>
            </div>

            {selectedCategory === "pending" && (
                <div className="requests-container">
                    <h2 className="requests-header">Pending Requests</h2>
                    <br/>
                    {error && <p className="error-message">{error}</p>}
                    <ul>
                        {filteredRequests.map((request) => (
                            <li key={request.requestId} className="request-item">
                                <p>Start Date: <span style={{fontWeight:"lighter"}}>{request.startDate}</span></p>
                                <p>End Date: <span style={{fontWeight:"lighter"}}>{request.endDate}</span></p>
                                <p>Reason: <span style={{fontWeight:"lighter"}}>{request.reason}</span></p>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            {selectedCategory === "approved" && (
                <div className="requests-container">
                    <h2 className="requests-header">Approved Requests</h2>
                    <br/>
                    {error && <p className="error-message">{error}</p>}
                    <div className="items">
                        <ul>
                            {approvedRequests.map((approvedRequest) => (
                                <li key={approvedRequest.approvedRequestId} className="request-item">
                                    <p>Start Date: <span style={{fontWeight:"lighter"}}>{approvedRequest.submittedRequest.startDate}</span></p>
                                    <p>End Date: <span style={{fontWeight:"lighter"}}>{approvedRequest.submittedRequest.endDate}</span></p>
                                    <p>Reason: <span style={{fontWeight:"lighter"}}>{approvedRequest.submittedRequest.reason}</span></p>
                                    <p>Approval Date: <span style={{fontWeight:"lighter"}}>{approvedRequest.approvalDate}</span></p>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            )}

            {selectedCategory === "rejected" && (
                <div className="requests-container">
                    <h2 className="requests-header">Rejected Requests</h2>
                    <br/>
                    {error && <p className="error-message">{error}</p>}
                    <ul>
                        {rejectedRequests.map((rejectedRequest) => (
                            <li key={rejectedRequest.rejectedRequestId} className="request-item">
                                <p>Start Date:<span style={{fontWeight:"lighter"}}> {rejectedRequest.submittedRequest.startDate}</span></p>
                                <p>End Date: <span style={{fontWeight:"lighter"}}>{rejectedRequest.submittedRequest.endDate} </span></p>
                                <p>Reason: <span style={{fontWeight:"lighter"}}>{rejectedRequest.submittedRequest.reason}</span></p>
                                <p>Rejection Date: <span style={{fontWeight:"lighter"}}>{rejectedRequest.rejectionDate}</span></p>
                                {rejectedRequest.rejectionReason && <p>Rejection Reason: <span style={{fontWeight:"lighter"}}>{rejectedRequest.rejectionReason}</span></p>}

                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default Requests;

import React, { useEffect, useState,useContext } from "react";
import { FeatureFlagContext } from "../../Components/FeatureFlagContext";
import "./SignedRequest.css";

const SignedRequest = () => {
    const userId = localStorage.getItem("idUser");
    const [signedRequests, setSignedRequests] = useState([]);
    const [canCancelRequests, setCanCancelRequests] = useState(true);
    // TODO: MOD IN FALSE

    const { checkFeature } = useContext(FeatureFlagContext);

    useEffect(() => {
        const fetchSignedRequests = async () => {
            try {
                const response = await fetch(`/api/v1/approved/signed/${userId}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    setSignedRequests(data);
                } else {
                    console.error("Failed to fetch signed requests.");
                }
            } catch (error) {
                console.error("Error fetching signed requests:", error);
            }
        };

        const fetchFeatureStatus = async () => {
            const isEnabled = await checkFeature("allow_cancel_signed_requests");
            setCanCancelRequests(isEnabled);
        };

        if (userId) {
            fetchSignedRequests();
            fetchFeatureStatus();
        } else {
            console.error("User ID not found.");
        }
    }, [userId]);


    const handleCancelRequest = async (requestId) => {
        try {
            const response = await fetch(`/api/v1/approved/${requestId}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            });

            if (response.ok) {
                setSignedRequests((prevRequests) =>
                    prevRequests.filter((request) => request.id !== requestId)
                );
                window.location.reload();
            } else {
                console.error("Failed to cancel the request.")
                alert("Failed to cancel the request.")
            }
        } catch (error) {
            console.error("Error canceling the request:", error);
            alert("Failed to cancel the request.")
        }
    };

    return (
            <div className="signed-requests-container">
                <h1 className="title">Signed Requests</h1>
                {signedRequests.length > 0 ? (
                    <ul className="signed-requests-list">
                        {signedRequests.map((request) => (
                            <li key={request.id} className="signed-request-item">
                                <div>
                                    <p className="scris"><strong>Employee:</strong> {request.submittedRequest.employee.firstName} {request.submittedRequest.employee.lastName}</p>
                                    <p className="scris"><strong>Start Date:</strong> {request.submittedRequest.startDate}</p>
                                    <p className="scris"><strong>End Date:</strong> {request.submittedRequest.endDate}</p>
                                    <p className="scris"><strong>Reason:</strong> {request.submittedRequest.reason}</p>
                                </div>
                                {/*{canCancelRequests && (*/}
                                    <button
                                        className="cancel-button"
                                        onClick={() => handleCancelRequest(request.approvedRequestId)}
                                    >
                                        Cancel request
                                    </button>
                                {/*)}*/}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No signed requests found.</p>
                )}
            </div>
    );
};

export default SignedRequest;

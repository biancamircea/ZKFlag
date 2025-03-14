import React, {useContext, useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import "./Home.css";
import {FeatureFlagContext} from "../../Components/FeatureFlagContext";

const Home = () => {
    const navigate = useNavigate();
    const [user, setUser] = useState({ firstName: "", lastName: "" });
    const userId = localStorage.getItem("idUser");
    const [isButtonVisible, setIsButtonVisible] = useState(true);
    const [isHead, setIsHead] = useState(false);
    const { featureFlags, checkFeature } = useContext(FeatureFlagContext);
    const [imageURL, setImageURL] = useState("");

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch(`/api/v1/employees/${userId}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    setUser({ firstName: data.firstName, lastName: data.lastName });
                }

                const headsResponse = await fetch("/api/v1/departaments/heads", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    },
                });

                const commandersResponse = await fetch("/api/v1/employees/commanders", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`,
                    },
                });

                if (headsResponse.ok && commandersResponse.ok) {
                    const heads = await headsResponse.json();
                    const commanders = await commandersResponse.json();

                    const isDeptHead = heads.some((head) => head.employeeId === parseInt(userId));
                    const isCommander = commanders.some((commander) => commander.employeeId === parseInt(userId));

                    if(isCommander || isDeptHead) setIsHead(true);

                    // const flagResponse = await fetch(`/api/v1/employees/show-requests-to-sign-button/${userId}`, {
                    //     method: 'GET',
                    //     headers: {
                    //         Authorization: `Bearer ${localStorage.getItem("token")}`,
                    //     },
                    // });
                    //
                    // if (flagResponse.ok) {
                    //     const flagData = await flagResponse.json();
                    //     setIsButtonVisible(flagData);
                    // }
                }
            } catch (error) {
                console.error("Error fetching user data:", error);
            }
        };

        if (userId) {
            fetchUserData();
        } else {
            console.error("User ID not found.");
        }

    }, [userId]);

    useEffect(() => {
        const fetchFeatureStatus = async () => {
            const featureName = "feature_imagine";
            const contextFields = [{ name: "port", value: `${process.env.REACT_APP_API_PORT}` }];
            const featureData = await checkFeature(featureName, contextFields);

            setImageURL(featureData.payload || "");
        };

        fetchFeatureStatus();
    }, [checkFeature]);


    return (
        <div className="home-page">
            <div className="home-container">
                <h1 style={{ color: "#3f3f3f" }}>Welcome, {user.firstName} {user.lastName}!</h1>
                <br/>
                <div className="button-group">
                    <button className="custom-button" onClick={() => navigate("/create_request")}>
                        Create new request
                    </button>
                    <button className="custom-button" onClick={() => navigate("/requests")}>
                        View your requests
                    </button>
                    {isButtonVisible && (
                        <button className="custom-button" onClick={() => navigate("/requests_for_sign")}>
                            See the requests to sign
                        </button>
                    )}
                    {isHead && (
                        <button className="custom-button" onClick={() => navigate("/signed_request")}>
                            See Signed Requests
                        </button>
                    )}
                </div>
                <br/>
                {imageURL && imageURL.trim() !== "" && (
                    <img src={imageURL} alt="Feature Image" style={{ maxWidth: "100%", height: "auto" }} />
                )}


            </div>
        </div>
    );
};

export default Home;

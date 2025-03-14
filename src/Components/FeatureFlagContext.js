import React, { createContext, useState, useEffect } from "react";

const FeatureFlagContext = createContext();

const FeatureFlagProvider = ({ children }) => {
    const [featureFlags, setFeatureFlags] = useState({});

    const checkFeature = async (featureName, contextFields) => {
        try {
            const response = await fetch("http://localhost:8080/client/evaluate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${process.env.REACT_APP_API_TOKEN}`,
                },
                body: JSON.stringify({
                    toggleName: featureName,
                    contextFields: contextFields,
                }),
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch feature ${featureName}`);
            }

            const data = await response.json();
            console.log(`Feature status for ${featureName}:`, data.enabled);

            const featureData = {
                enabled: data.enabled,
                payload: data.payload,
            };

            return featureData;
        } catch (error) {
            console.error("Error checking feature:", error);
            return { enabled: false, payload: null };
        }
    };

    useEffect(() => {
        const fetchFeatureStatus = async () => {
            const featureName = "feature_backend";
            const featureData = await checkFeature(featureName, []);

            console.log("Feature Data pt feature backend:", featureData);
            setFeatureFlags((prev) => ({
                ...prev,
                [featureName]: { enabled: featureData.enabled, payload: featureData.payload },
            }));
        };
        fetchFeatureStatus();
    }, []);

    return (
        <FeatureFlagContext.Provider value={{ featureFlags, checkFeature }}>
            {children}
        </FeatureFlagContext.Provider>
    );
};

export { FeatureFlagContext, FeatureFlagProvider };

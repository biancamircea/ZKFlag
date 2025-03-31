import React, { createContext, useState } from "react";
import * as snarkjs from "snarkjs";
import { fetchConstraints, sendCombinedRequest } from "./api";

const FeatureFlagContext = createContext();

const FeatureFlagProvider = ({ children }) => {
    const [featureFlags, setFeatureFlags] = useState({});


    const checkFeature = async (featureName, contextFields) => {
        try {
            const constraintsData = await fetchConstraints(featureName);
            console.log("constraint data: ",constraintsData)

            const normalContextFields = [];
            const proofs = [];

            for (const constraint of constraintsData) {
                if (constraint.isConfidential === 1) {
                    try {
                        const contextField = contextFields.find(field => field.name === constraint.contextName);
                        if (!contextField) {
                           continue;
                        }

                        const threshold = Number(constraint.values[0]);
                        const value = Number(contextField.value);

                        let operator;
                        switch(constraint.operator) {
                            case "GREATER_THAN": operator = 1; break;
                            case "LESS_THAN": operator = 0; break;
                            case "IN": operator = 2; break;
                            case "NOT_IN": operator = 3; break;
                            default: operator = 0;
                        }

                        const { proofJson, publicSignalsJson } = await generateProof(value, threshold, operator);

                        proofs.push({
                            name: constraint.contextName,
                            proof: {
                                proof: JSON.parse(proofJson),
                                publicSignals: JSON.parse(publicSignalsJson)
                            }
                        });

                    } catch (error) {
                        console.error(`Error generating proof for constraint ${constraint.contextName}:`, error);
                        return { enabled: false, payload: null };
                    }
                } else {
                    const contextField = contextFields.find(field => field.name === constraint.contextName);
                    if (contextField) {
                        const exists = normalContextFields.some(field => field.name === constraint.contextName);
                        if (!exists) {
                            normalContextFields.push({
                                name: constraint.contextName,
                                value: contextField.value
                            });
                        }
                    }
                }
            }

            const requestData = {
                toggleName: featureName,
                contextFields: normalContextFields.length > 0 ? normalContextFields : [],
                proofs: proofs.length > 0 ? proofs : []
            };
            console.log("req data:", requestData)

            const verificationData = await sendCombinedRequest(requestData);
            console.log(`Feature status for ${featureName} (combined):`, verificationData.enabled);

            return {
                enabled: verificationData.enabled,
                payload: verificationData.payload,
            };
        } catch (error) {
            console.error("Error checking feature with combined constraints:", error);
            return { enabled: false, payload: null };
        }
    };

    const generateProof = async (value, threshold, operator) => {
        const wasmPath = "zkp/age_check_plonk.wasm";
        const zkeyPath = "zkp/age_check_plonk.zkey";

        const input = {
            val: value,
            threshold: threshold,
            operation: operator
        };

        try {
            const { proof, publicSignals } = await snarkjs.plonk.fullProve(input, wasmPath, zkeyPath);
            return {
                proofJson: JSON.stringify(proof),
                publicSignalsJson: JSON.stringify(publicSignals)
            };
        } catch (error) {
            console.error("Error generating proof:", error);
            throw error;
        }
    };

    // useEffect(() => {
    //     const fetchFeatureStatus = async () => {
    //         const featureName = "toggle2";
    //         const contextFields = [{name: "conf", value:"14"},{name:"age", value:"13"},{name: "nou",value:"100"}];
    //         const featureData = await checkFeatureCombined(featureName, contextFields);
    //
    //         console.log("Feature Data:", featureData);
    //         setFeatureFlags((prev) => ({
    //             ...prev,
    //             [featureName]: {
    //                 enabled: featureData.enabled,
    //                 payload: featureData.payload
    //             },
    //         }));
    //     };
    //     fetchFeatureStatus();
    // }, []);

    return (
        <FeatureFlagContext.Provider value={{
            featureFlags,
            checkFeature
        }}>
            {children}
        </FeatureFlagContext.Provider>
    );
};

export { FeatureFlagContext, FeatureFlagProvider };
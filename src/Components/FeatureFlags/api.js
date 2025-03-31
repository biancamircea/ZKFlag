const baseUrl="/client"


export async function evaluateToggle(featureName, contextFields) {
    try{
        const response = await fetch(`${baseUrl}/evaluate`, {
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

        return await response.json();
    }catch (error) {
        console.error("Error checking feature:", error);
        return { enabled: false, payload: null };
    }

    return false;

}


export async function fetchConstraints(featureName) {
    try {
        const constraintsResponse = await fetch(`${baseUrl}/constraints`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: featureName,
        });

        if (!constraintsResponse.ok) {
            throw new Error(`Failed to fetch constraints for feature ${featureName}`);
        }
        return await constraintsResponse.json();
    } catch (error) {
        console.error("Error fetching constraints:", error);
        return [];
    }
}


export async function sendProof(featureName,proofJson,publicSignalsJson){
    try{
    const verificationResponse = await fetch(baseUrl+"/evaluateZKP", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${process.env.REACT_APP_API_TOKEN}`,
        },
        body: JSON.stringify({
            toggleName: featureName,
            proof: {
                proof: JSON.parse(proofJson),
                publicSignals: JSON.parse(publicSignalsJson),
            },
        }),
    });

    if (!verificationResponse.ok) {
        throw new Error(`Failed to verify proof for feature ${featureName}`);
    }

    return await verificationResponse.json();
    }catch (error) {
        console.error("Error sending proof:", error);
        return false;
    }
}

export async function sendCombinedRequest(requestData) {
    try {
        const verificationResponse = await fetch(`${baseUrl}/evaluate`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(requestData),
        });
        return await verificationResponse.json();
    } catch (error) {
        console.error("Error sending combined request:", error);
        throw error;
    }
}
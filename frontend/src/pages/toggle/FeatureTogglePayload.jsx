import React from 'react';
import {useEffect, useState} from 'react';
import {useOutletContext, useParams} from "react-router-dom";
import FeatureTogglePayloadItem from "../../components/ui/toggle/FeatureTogglePayloadItem.jsx";
import {
    addPayloadInToggleEnv,
    deletePayloadFromToggleEnv,
    updatePayloadInToggleEnv
} from "../../api/featureToggleApi.js";
import {toast} from "react-toastify";
import {getAllEnvironmentsFromInstance, getToggleEnvironments} from "../../api/instanceApi";

function FeatureTogglePayload(props) {
    const {toggle, addPayload, removePayload} = useOutletContext()
    const {projectId, featureId,instanceId} = useParams()
    const [environments, setEnvironments] = useState([]);

    useEffect(() => {
        async function fetchEnvironments() {
            try {
                const data = await getToggleEnvironments(instanceId, featureId);
                setEnvironments(data);
            } catch (error) {
                console.error("Error fetching environments:", error);
            }
        }

        fetchEnvironments();
    }, [instanceId]);

    async function addPayloadHandler(envId, data) {
        const response = await addPayloadInToggleEnv(projectId, featureId,instanceId, envId, data)
        if (response) {
            addPayload(response)
            toast.success("Payload created.")
        } else {
            toast.error("Operation failed.")
        }
    }

    async function updatePayloadHandler(envId, data) {
        const response = await updatePayloadInToggleEnv(projectId, featureId,instanceId, envId, data)
        if (response) {
            addPayload(response)
            toast.success("Payload updated.")
        } else {
            toast.error("Operation failed.")
        }
    }

    async function deletePayloadHandler(envId) {
        const response = await deletePayloadFromToggleEnv(projectId, featureId, envId,instanceId)
        if (response) {
            removePayload(envId)
            toast.success("Payload deleted.")
        } else {
            toast.error("Operation failed.")
        }
    }

    console.log(environments);

    const payloadItems = environments.map(el => {
        return (
            <FeatureTogglePayloadItem
                key={el.id}
                environmentName={el.name}
                enabledValue={el.enabledValue}
                disabledValue={el.disabledValue}
                updateHandler={(data) => updatePayloadHandler(el.environmentId, data)}
                addHandler={(data) => addPayloadHandler(el.environmentId, data)}
                deleteHandler={() => deletePayloadHandler(el.environmentId)}
            />

        )
    })

    return (
        <div className={"feature-toggle-overview-section-right payload"}>
            {payloadItems}
        </div>
    );
}

export default FeatureTogglePayload;
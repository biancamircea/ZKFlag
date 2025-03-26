import React, { useEffect, useState } from 'react';
import { useOutletContext, useParams } from "react-router-dom";
import FeatureTogglePayloadItem from "../../components/ui/toggle/FeatureTogglePayloadItem.jsx";
import {
    addPayloadInToggleEnv,
    deletePayloadFromToggleEnv,
    updatePayloadInToggleEnv
} from "../../api/featureToggleApi.js";
import { toast } from "react-toastify";
import { getToggleEnvironments } from "../../api/instanceApi";
import EmptyList from "../../components/ui/common/EmptyList.jsx";

function FeatureTogglePayload(props) {
    const { toggle } = useOutletContext();
    const { projectId, featureId, instanceId } = useParams();
    const [environments, setEnvironments] = useState([]);


    const refreshEnvironments = async () => {
        try {
            const data = await getToggleEnvironments(instanceId, featureId);
            setEnvironments(data);
        } catch (error) {
            console.error("Error fetching environments:", error);
            toast.error("Failed to refresh data");
        }
    };


    useEffect(() => {
        refreshEnvironments();
    }, [instanceId, featureId]);

    const addPayloadHandler = async (envId, data) => {
        try {
            await addPayloadInToggleEnv(projectId, featureId, instanceId, envId, data);
            await refreshEnvironments();
            toast.success("Payload created successfully");
        } catch (error) {
            toast.error("Failed to create payload");
        }
    };


    const updatePayloadHandler = async (envId, data) => {
        try {
            await updatePayloadInToggleEnv(projectId, featureId, instanceId, envId, data);
            await refreshEnvironments();
            toast.success("Payload updated successfully");
        } catch (error) {
            toast.error("Failed to update payload");
        }
    };


    const deletePayloadHandler = async (envId) => {
        try {
            await deletePayloadFromToggleEnv(projectId, featureId, envId, instanceId);
            await refreshEnvironments();
            toast.success("Payload deleted successfully");
        } catch (error) {
            toast.error("Failed to delete payload");
        }
    };

    return (
        <div className={"feature-toggle-overview-section-right payload"}>
            {environments.length === 0 ? (
            <EmptyList
                resource={"environment"}
                recommend={"Activate environments from settings to manage feature toggles effectively."}
            />
            ):
            environments.map(env => (
                <FeatureTogglePayloadItem
                    key={env.id}
                    environmentName={env.name}
                    enabledValue={env.enabledValue}
                    disabledValue={env.disabledValue}
                    updateHandler={(data) => updatePayloadHandler(env.environmentId, data)}
                    addHandler={(data) => addPayloadHandler(env.environmentId, data)}
                    deleteHandler={() => deletePayloadHandler(env.environmentId)}
                />
            ))
            }
        </div>
    );
}

export default FeatureTogglePayload;
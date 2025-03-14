import React, { Suspense, useState, useEffect } from 'react';
import Tooltip from "@mui/material/Tooltip";
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import { useOutletContext } from "react-router-dom";
import {getConstraintFromToggleEnvironment, getConstraintValues} from "../../../api/featureToggleApi.js";
import LoadingBanner from "../common/LoadingBanner.jsx"; // Adaugă un loading banner (componentă de încărcare)
import {getToggleEnvironment} from "../../../api/featureToggleApi.js";

function EditConstraintDialog({context, operator, values, submitHandler, instanceId, constraintId,environmentId,toggleId}) {
    const { contextFields } = useOutletContext()
    const [open, setOpen] = useState(false);
    const [pvalues, setPValues] = useState(values);
    const [isLoading, setIsLoading] = useState(true);
    const [elementWithName, setElementWithName] = useState(null);

    useEffect(() => {
        if(instanceId) {
            setElementWithName(contextFields["context-fields"].find((element) => element.name === context.contextName));
        }else{
            setElementWithName(contextFields.find((element) => element.name === context.contextName));
        }

        async function fetchConstraintValues() {
            try {
                if (instanceId) {
                   const toggleEnv=await getToggleEnvironment( toggleId, instanceId, environmentId)

                    const constraint_values = await getConstraintValues(constraintId);


                    const filteredValues = Array.isArray(constraint_values["constraint-values"])
                        ? constraint_values["constraint-values"].filter(value => value.toggle_environment_id !== null && value.toggle_environment_id !== undefined && toggleEnv.id==value.toggle_environment_id)
                        : [];

                    const structuredValues = filteredValues.map(value => ({
                        id: value.id,
                        name: value.value
                    }));

                    setPValues(structuredValues);

                } else {
                    setPValues(values);
                }
            } catch (error) {
                console.error("Failed to fetch constraint values:", error);
            } finally {
                setIsLoading(false);
            }
        }

        fetchConstraintValues();
    }, [instanceId, values, context, contextFields]);


    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = (event, reason) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    return (
        <div onClick={(event) => event.stopPropagation()} className={"action-icon-wrapper"}>
            <Tooltip title={"Edit"} arrow>
                <img src={"/images/edit.png"}
                     alt={"Edit"}
                     className={"action-icon"}
                     onClick={(event) => {
                         event.stopPropagation()
                         handleClickOpen()
                     }}
                />
            </Tooltip>

            <Suspense fallback={<LoadingBanner />}>
                {pvalues &&  elementWithName &&(
                    <FeatureToggleAddConstraintDialog
                        open={open}
                        onClose={handleClose}
                        contextFields={contextFields}
                        pContext={elementWithName}
                        pOperator={operator}
                        pValues={pvalues}
                        submitHandler={submitHandler}
                        edit
                        instanceId={instanceId}
                    />
                )}
            </Suspense>

        </div>
    );
}

export default EditConstraintDialog;

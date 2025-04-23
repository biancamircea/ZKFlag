import React, { Suspense, useState, useEffect } from 'react';
import Tooltip from "@mui/material/Tooltip";
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import { useOutletContext } from "react-router-dom";
import {
    getConstraintFromToggleEnvironment,
    getConstraintValues,
    getTypeByToggleId
} from "../../../api/featureToggleApi.js";
import LoadingBanner from "../common/LoadingBanner.jsx";
import {getToggleEnvironment} from "../../../api/featureToggleApi.js";

function EditConstraintDialog({context, operator, values, submitHandler, instanceId, constraintId,environmentId,toggleId}) {
    const { contextFields } = useOutletContext()
    const [open, setOpen] = useState(false);
    const [pvalues, setPValues] = useState(values);
    const [isLoading, setIsLoading] = useState(true);
    const [elementWithName, setElementWithName] = useState(null);
    const [filteredContextFields, setFilteredContextFields] = useState([]);

    useEffect(() => {
        const fetchToggleType = async () => {
            try {
                const type = await getTypeByToggleId(toggleId);
                if  (type === 0 || type === 2) {
                    setFilteredContextFields(contextFields.filter(field => !field.isConfidential));
                } else {
                    setFilteredContextFields(contextFields);
                }
            } catch (error) {
                console.error("Failed to fetch toggle type:", error);
                setFilteredContextFields(contextFields);
            }
        };

        if (toggleId) {
            fetchToggleType();
        }
    }, [toggleId, contextFields]);



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

                    const structuredValues = filteredValues.map(value => value.value);

                    structuredValues.reverse();

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
                        contextFields={filteredContextFields}
                        pContext={elementWithName}
                        pOperator={operator}
                        pValues={pvalues}
                        submitHandler={submitHandler}
                        edit
                        instanceId={instanceId}
                        isConfidential={elementWithName.isConfidential}
                    />
                )}
            </Suspense>

        </div>
    );
}

export default EditConstraintDialog;

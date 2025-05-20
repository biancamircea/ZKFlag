import React from 'react';
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import {useOutletContext} from "react-router-dom";
import Tooltip from "@mui/material/Tooltip";
import { useState, useEffect } from "react";
import {getTypeByToggleId} from "../../../api/featureToggleApi.js";

function AddConstraintButton({ submitHandler, toggleId, disabled = false, tooltip = "" }) {
    const { contextFields } = useOutletContext();
    const [open, setOpen] = useState(false);
    const [filteredContextFields, setFilteredContextFields] = useState([]);

    useEffect(() => {
        const fetchToggleType = async () => {
            try {
                const type = await getTypeByToggleId(toggleId);
                if (type === 0 || type === 2) {
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

    const handleClickOpen = () => {
        if (!disabled) {
            setOpen(true);
        }
    };

    const handleClose = (event, reason) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    return (
        <>
            <Tooltip title={disabled ? tooltip : "Add a group of constraints"} arrow>
                <button
                    className="center-aligned-button"
                    onClick={handleClickOpen}
                    disabled={disabled}
                    style={{
                        cursor: disabled ? "not-allowed" : "pointer",
                        opacity: disabled ? 0.5 : 1
                    }}
                >
                    + Add constraint
                </button>
            </Tooltip>

            <FeatureToggleAddConstraintDialog
                open={open}
                onClose={handleClose}
                contextFields={filteredContextFields}
                submitHandler={submitHandler}
            />
        </>
    );
}

export default AddConstraintButton;

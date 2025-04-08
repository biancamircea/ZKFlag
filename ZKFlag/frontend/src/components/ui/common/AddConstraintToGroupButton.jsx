import React, {useEffect, useState} from 'react';
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import {useOutletContext} from "react-router-dom";
import AddConstraintIcon from "./AddConstraintIcon.jsx";
import {getTypeByToggleId} from "../../../api/featureToggleApi.js";

function AddConstraintToGroupButton({ submitHandler, groupId, toggleId }) {
    const { contextFields } = useOutletContext();
    const [open, setOpen] = React.useState(false);
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


    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = (event, reason) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    return (
        <>
            <AddConstraintIcon onClick={() => setOpen(true)} />

            <FeatureToggleAddConstraintDialog
                open={open}
                onClose={handleClose}
                contextFields={filteredContextFields}
                submitHandler={(data) => submitHandler(data, groupId)}
            />
        </>
    );
}

export default AddConstraintToGroupButton;
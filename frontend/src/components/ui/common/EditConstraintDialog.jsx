import React from 'react';
import Tooltip from "@mui/material/Tooltip";
import FeatureToggleAddConstraintDialog from "../toggle/FeatureToggleAddConstraintDialog.jsx";
import {useOutletContext} from "react-router-dom";

function EditConstraintDialog({context, operator, values, submitHandler}) {
    const { contextFields } = useOutletContext()
    const [open, setOpen] = React.useState(false);
    const elementWithName = contextFields.find((element) => element.name === context);
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
                <img src={"/src/assets/images/edit.png"}
                     alt={"Edit"}
                     className={"action-icon"}
                     onClick={(event) => {
                         event.stopPropagation()
                         handleClickOpen()
                     }}
                />
            </Tooltip>
            <FeatureToggleAddConstraintDialog
                open={open}
                onClose={handleClose}
                contextFields={contextFields}
                pContext={elementWithName?.id}
                pOperator={operator}
                pValues={values}
                submitHandler={submitHandler}
                edit
            />
        </div>
    );
}

export default EditConstraintDialog;
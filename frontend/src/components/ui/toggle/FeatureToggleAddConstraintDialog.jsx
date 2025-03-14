import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    OutlinedInput,
    Select
} from "@mui/material";
import OperatorField from "./OperatorField.jsx";
import ConstraintValuesList from "./ConstraintValuesList.jsx";
import { Form } from "react-router-dom";
import { toast } from "react-toastify";

function FeatureToggleAddConstraintDialog({ onClose, open, contextFields, submitHandler, pContext, pOperator, pValues, edit, instanceId }) {
    const [context, setContext] = useState(pContext || '');
    const [values, setValues] = useState( []);

    if(instanceId) {
        useEffect(() => {
            if (pValues) {
                setValues(pValues.map((value) => value.name));
            }
        }, [pValues]);
    }else{
        useEffect(() => {
            if (pValues) {
                setValues(pValues);
            }
        }, [pValues]);

    }

    useEffect(() => {
        if (pContext) {
            setContext(pContext.id);
        }
    }, [pContext]);



    const handleClose = () => {
        onClose();
        if (!edit) {
            setContext('');
            setValues([]);
        }
    };

    const handleChange = (event) => {
        setContext(event.target.value || '');
    };

    const handleSubmit = (event) => {

        event.preventDefault();
        const formData = new FormData(event.target);
        const contextValue = formData.get("context");
        const operator = formData.get("operator");

        if (instanceId == null) {
            if (!contextValue || !operator || values.length === 0) {
                toast.error("Please fill empty fields!");
            } else {
                const elementWithId = contextFields.find((obj) => obj.id === contextValue);
                submitHandler({ contextName: elementWithId?.name, operator, values });
            }
        } else {
            submitHandler({ contextName: pContext.name, operator: pOperator, values });
        }

        handleClose();
    };

    const contextFieldsDropDown = Array.isArray(contextFields) ? contextFields.map((el) => (
        <MenuItem value={el.id} key={el.id}>{el.name}</MenuItem>
    )) : [];

    return (
        <Dialog onClose={handleClose} open={open}>
            <DialogTitle>{edit ? "Edit constraint" : "Add new constraint"}</DialogTitle>
            <Form method={"post"} onSubmit={handleSubmit}>
                <DialogContent>
                    {instanceId == null &&
                        <FormControl sx={{ m: 1, minWidth: 250 }}>
                            <InputLabel id="demo-dialog-select-label">Context</InputLabel>
                            <Select
                                labelId="demo-dialog-select-label"
                                id="demo-dialog-select"
                                name="context"
                                value={context}
                                onChange={handleChange}
                                input={<OutlinedInput label="Context" />}
                            >
                                {contextFieldsDropDown.length === 0 ?
                                    <MenuItem disabled>No context field</MenuItem>
                                    : contextFieldsDropDown
                                }
                            </Select>
                        </FormControl>
                    }

                    {instanceId == null && <OperatorField defaultOperator={pOperator} />}

                    <ConstraintValuesList
                        values={values}
                        setValues={setValues}
                    />
                </DialogContent>
                <DialogActions>
                    <button
                        className={"reverse-btn"}
                        onClick={(event) => {
                            event.preventDefault();
                            handleClose();
                        }}
                    >
                        Cancel
                    </button>
                    <button type={"submit"}>
                        {edit ? "Update" : "Save"}
                    </button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default FeatureToggleAddConstraintDialog;

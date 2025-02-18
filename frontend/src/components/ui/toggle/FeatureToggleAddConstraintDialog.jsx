import React, {useState} from 'react';
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem, OutlinedInput,
    Select
} from "@mui/material";
import OperatorField from "./OperatorField.jsx";
import ConstraintValuesList from "./ConstraintValuesList.jsx";
import {Form} from "react-router-dom";
import {toast} from "react-toastify";

function FeatureToggleAddConstraintDialog( { onClose, open, contextFields, submitHandler, pContext, pOperator, pValues, edit }) {
    const [context, setContext] = useState(pContext ? pContext : '');
    const [values, setValues] = useState(pValues ? pValues : []);
    const handleClose = () => {
        onClose();
        if(!edit){
            setContext(''); // Reset context state
            setValues([]); // Reset values state
        }
    };
    const handleChange = (event) => {
        setContext(Number(event.target.value) || '');
    };

    function handleSubmit(event){
        event.preventDefault()
        const formData = new FormData(event.target);
        const context = formData.get("context");
        const operator = formData.get("operator");

        if(!context || !operator || (values.length === 0)){
            toast.error("Please fill empty fields!");
        } else {
            const elementWithId = contextFields.find((obj) => obj.id === Number(context));
            submitHandler({contextName: elementWithId.name, operator, values})
            handleClose()
        }
    }


    const contextFieldsDropDown = contextFields?.map((el) => {
        return (
            <MenuItem value={el.id} key={el.id}>{el.name}</MenuItem>
        )
    })

    return (
        <Dialog onClose={handleClose} open={open}>
            <DialogTitle>{edit ? "Edit constraint" : "Add new constraint"} </DialogTitle>
            <Form
                method={"post"}
                onSubmit={handleSubmit}>
                <DialogContent>
                    <FormControl sx={{ m: 1, minWidth: 250 }}>
                        <InputLabel id="demo-dialog-select-label">Context</InputLabel>
                        <Select
                            labelId="demo-dialog-select-label"
                            id="demo-dialog-select"
                            name={"context"}
                            value={context}
                            onChange={handleChange}
                            input={<OutlinedInput label="Context" />}
                        >
                            {contextFieldsDropDown?.length === 0 ?
                                <h4>No context field</h4>
                                : contextFieldsDropDown
                            }
                        </Select>
                    </FormControl>
                    <OperatorField
                        defaultOperator={pOperator}
                    />
                    <ConstraintValuesList
                        values={values}
                        setValues={setValues}
                    />
                </DialogContent>
                <DialogActions>
                    <button
                        className={"reverse-btn"}
                        onClick={event => {
                            event.preventDefault()
                            handleClose()
                        }}>
                        Cancel
                    </button>
                    <button
                        type={"submit"}>
                        {edit ? "Update" : "Save"}
                    </button>
                </DialogActions>

            </Form>
        </Dialog>
    );
}

export default FeatureToggleAddConstraintDialog;
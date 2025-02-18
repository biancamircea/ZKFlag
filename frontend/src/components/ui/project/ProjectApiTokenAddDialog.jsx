import React, {useState} from 'react';
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel, MenuItem,
    OutlinedInput,
    Select
} from "@mui/material";
import {Form} from "react-router-dom";
import WarningField from "../common/WarningField.jsx";
import {toast} from "react-toastify";

function ProjectApiTokenAddDialog({ onClose, open, submitHandler, environments, tokensName,projectId,instanceId }) {
    const [disableSubmit, setDisableSubmit] = useState(false)
    const [env, setEnv] = useState('');

    const handleClose = () => {
        onClose();
        setDisableSubmit(false)
        setEnv('')
    };
    const handleChange = (event) => {
        setEnv(Number(event.target.value) || '');
    };
    function handleNameInput(event) {
        const name = event.target.value;
        if (tokensName.includes(name)) {
            setDisableSubmit(true);
        } else {
            setDisableSubmit(false);
        }
    }


    const environmentsDropDown = environments.map((el) => {
        return (
            <MenuItem value={el.id} key={el.id}>{el.name}</MenuItem>
        )
    })

    function handleSubmit(event) {
        event.preventDefault()
        const formData = new FormData(event.target);
        const name = formData.get("name");
        const environment = formData.get("environment");

        let sanitizedName = null;
        if (name) {
            sanitizedName = name.trim()
        }
        if (!sanitizedName || !env) {
            // One or both fields are empty
            toast.error("Please check empty fields!");
        } else {
            if (disableSubmit) {
                toast.error("Please check invalid fields.");
            } else {
                submitHandler({ name, environmentId: environment,projectId: projectId,instanceId: instanceId });
                handleClose()
            }
        }
    }

    return (
        <Dialog onClose={handleClose} open={open}>
            <DialogTitle>Generate Api Token</DialogTitle>
            <Form
                method={"post"}
                onSubmit={handleSubmit}
            >
                <DialogContent>
                    <div className={"create-form-fields"}>
                        <div className={"create-form-field-item"}>
                            <label htmlFor={"name"}>What is your Api Token name?</label>
                            {
                                disableSubmit
                                &&
                                <WarningField
                                    message={"*token with that name already exists"}
                                />
                            }
                            <input
                                className={disableSubmit ? "invalid" : ""}
                                id={"name"}
                                name={"name"}
                                type={"text"}
                                placeholder={"Token name"}
                                onInput={handleNameInput}
                            />
                        </div>

                    </div>
                    <FormControl sx={{ m: 1, minWidth: 250 }}>
                        <InputLabel id="demo-dialog-select-label">Environment</InputLabel>
                        <Select
                            labelId="demo-dialog-select-label"
                            id="demo-dialog-select"
                            name={"environment"}
                            value={env}
                            onChange={handleChange}
                            input={<OutlinedInput label="Environment" />}
                        >{
                            environmentsDropDown?.length === 0
                                ? <h4>No environment enabled</h4>
                                : environmentsDropDown
                        }</Select>
                    </FormControl>


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
                        Generate
                    </button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default ProjectApiTokenAddDialog;
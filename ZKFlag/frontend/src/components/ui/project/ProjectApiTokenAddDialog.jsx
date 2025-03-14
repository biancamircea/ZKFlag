import React, { useState } from 'react';
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
import { Form } from "react-router-dom";
import WarningField from "../common/WarningField.jsx";
import { toast } from "react-toastify";

function ProjectApiTokenAddDialog({ onClose, open, submitHandler, environments, tokensName, projectId, instanceId }) {
    const [disableSubmit, setDisableSubmit] = useState(false);
    const [env, setEnv] = useState('');
    const [tokenType, setTokenType] = useState('');

    const handleClose = () => {
        onClose();
        setDisableSubmit(false);
        setEnv('');
        setTokenType('');
    };

    const handleEnvChange = (event) => {
        setEnv(event.target.value || '');
    };

    const handleTokenTypeChange = (event) => {
        setTokenType(event.target.value);
    };

    function handleNameInput(event) {
        const name = event.target.value;
        setDisableSubmit(tokensName.includes(name));
    }

    const environmentsDropDown = environments.map((el) => (
        <MenuItem value={el.id} key={el.id}>{el.name}</MenuItem>
    ));

    function handleSubmit(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const name = formData.get("name");
        const environment = formData.get("environment");

        const tokenType = formData.get("tokenType");
        if(tokenType ===""){
            toast.error("Please check empty fields!");
        }

        let sanitizedName = name?.trim() || null;
        if (!sanitizedName || !env ) {
            toast.error("Please check empty fields!");
        } else {
            if (disableSubmit) {
                toast.error("Please check invalid fields.");
            } else {
                submitHandler({ name, environmentId: environment, type: Number(tokenType),projectId: projectId, instanceId: instanceId });
                handleClose();
            }
        }
    }

    return (
        <Dialog onClose={handleClose} open={open}>
            <DialogTitle>Generate Api Token</DialogTitle>
            <Form method={"post"} onSubmit={handleSubmit}>
                <DialogContent>
                    <div className={"create-form-fields"}>
                        <div className={"create-form-field-item"}>
                            <label htmlFor={"name"}>What is your Api Token name?</label>
                            {disableSubmit && <WarningField message={"*token with that name already exists"} />}
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
                    <div style={{display: "flex", justifyContent: "center"}}>
                    <FormControl sx={{ m: 1, minWidth: 250 }}>
                        <InputLabel id="environment-label">Environment</InputLabel>
                        <Select
                            labelId="environment-label"
                            id="environment-select"
                            name="environment"
                            value={env}
                            onChange={handleEnvChange}
                            input={<OutlinedInput label="Environment" />}
                        >
                            {environmentsDropDown.length === 0 ? (
                                <MenuItem disabled>No environment enabled</MenuItem>
                            ) : environmentsDropDown}
                        </Select>
                    </FormControl>
                    </div>
                    <div style={{display: "flex", justifyContent: "center"}}>
                    <FormControl sx={{ m: 1, minWidth: 250 }}>
                        <InputLabel id="token-type-label">Token Type</InputLabel>
                        <Select
                            labelId="token-type-label"
                            id="token-type-select"
                            name="tokenType"
                            value={tokenType}
                            onChange={handleTokenTypeChange}
                            input={<OutlinedInput label="Token Type" />}
                        >
                            <MenuItem value="0">Frontend</MenuItem>
                            <MenuItem value="1">Backend</MenuItem>
                            <MenuItem value="2">Both</MenuItem>
                        </Select>
                    </FormControl>
                    </div>
                </DialogContent>
                <DialogActions>
                    <button className={"reverse-btn"} onClick={(event) => {
                        event.preventDefault();
                        handleClose();
                    }}>
                        Cancel
                    </button>
                    <button type={"submit"}>Generate</button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default ProjectApiTokenAddDialog;

import React from 'react';
import CancelButton from "../ui/common/CancelButton.jsx";
import {Form} from "react-router-dom";
import WarningField from "../ui/common/WarningField.jsx";
import {
    FormControl,
    InputLabel,
    MenuItem,
    OutlinedInput,
    Select
} from "@mui/material";

function CreateContextFieldForm({handleSubmit, handleNameInput, disableSubmit,isConfidential, handleConfidentialChange}) {
    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}>
            <div className={"create-form-fields"}>
                <span className={"title"}>Create context field</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your context name?</label>
                    {
                        disableSubmit
                        &&
                        <WarningField
                            message={"*context field with that name already exists"}
                        />
                    }
                    <input
                        className={disableSubmit ? "invalid" : ""}
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Context name"}
                        onInput={handleNameInput}
                    />
                </div>


                <FormControl sx={{ m: 1, width:"30em"}}>
                    {/*<InputLabel id="confidential-select-label">Confidential</InputLabel>*/}
                    <label htmlFor="confidential-select-label">Does this context field need ZKP verification?</label>
                    <Select
                        labelId="confidential-select-label"
                        id="confidential-select"
                        name="isConfidential"
                        value={isConfidential}
                        onChange={handleConfidentialChange}
                        input={<OutlinedInput label="Confidential" />}
                    >
                        <MenuItem value={1}>Yes</MenuItem>
                        <MenuItem value={0}>No</MenuItem>
                    </Select>
                </FormControl>

                <div className={"create-form-field-item"}>
                    <label htmlFor={"description"}>What is this context used for?</label>
                    <textarea
                        id={"description"}
                        name={"description"}
                        placeholder={"Context description"}
                    />
                </div>
            </div>
            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Create context
                </button>
            </div>
        </Form>
    );
}

export default CreateContextFieldForm;
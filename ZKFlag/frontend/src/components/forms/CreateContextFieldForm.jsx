import React, { useState } from 'react';
import CancelButton from "../ui/common/CancelButton.jsx";
import {Form} from "react-router-dom";
import WarningField from "../ui/common/WarningField.jsx";
import {
    FormControl,
    InputLabel,
    MenuItem,
    OutlinedInput,
    Select,
    FormControlLabel,
    Checkbox
} from "@mui/material";

function CreateContextFieldForm({handleSubmit, handleNameInput, disableSubmit, isConfidential, handleConfidentialChange}) {
    const [isLocationField, setIsLocationField] = useState(false);

    const handleLocationChange = (event) => {
        const isLocation = event.target.checked;
        setIsLocationField(isLocation);
        if (isLocation) {
            handleConfidentialChange({ target: { value: 2 } });
        } else {
            handleConfidentialChange({ target: { value: 0 } });
        }
    };

    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}>
            <div className={"create-form-fields"}>
                <span className={"title"}>Create context field</span>
                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>What is your context name?</label>
                    {disableSubmit && <WarningField message={"*context field with that name already exists"} />}
                    <input
                        className={disableSubmit ? "invalid" : ""}
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        placeholder={"Context name"}
                        onInput={handleNameInput}
                    />
                </div>

                {/* Checkbox pentru locație */}
                <div className={"create-form-field-item"}>
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={isLocationField}
                                onChange={handleLocationChange}
                                name="isLocation"
                                color="primary"
                            />
                        }
                        label="Is this a location field?"
                    />
                </div>

                <input
                    type="hidden"
                    name="isConfidential"
                    value={isLocationField ? 2 : isConfidential}
                />

                <FormControl sx={{ m: 1, width: "30em" }}>
                    <label htmlFor="confidential-select-label">
                        Does this context field need private verification?
                    </label>
                    <Select
                        labelId="confidential-select-label"
                        id="confidential-select"
                        value={isLocationField ? 2 : isConfidential}
                        input={<OutlinedInput label="Confidential" />}
                        disabled={isLocationField}
                        onChange={handleConfidentialChange}
                    >
                        <MenuItem value={1}>Yes</MenuItem>
                        <MenuItem value={0}>No</MenuItem>
                        {isLocationField && (
                            <MenuItem value={2} disabled>
                                Location-based (automatic)
                            </MenuItem>
                        )}
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
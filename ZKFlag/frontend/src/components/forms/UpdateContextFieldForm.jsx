import React from 'react';
import {Form} from "react-router-dom";
import CancelButton from "../ui/common/CancelButton.jsx";
import {
    FormControl,
    MenuItem,
    OutlinedInput,
    Select
} from "@mui/material";

function UpdateContextFieldForm({handleSubmit, formData, handleChange}) {
    return (
        <Form
            className={"create-form-container"}
            method={"post"}
            onSubmit={handleSubmit}>
            <div className={"create-form-fields"}>
                <span className={"title"}>Edit context field</span>

                <div className={"create-form-field-item"}>
                    <label htmlFor={"name"}>Context name</label>
                    <input
                        id={"name"}
                        name={"name"}
                        type={"text"}
                        value={formData.name}
                        readOnly
                        disabled
                    />
                </div>

                <div className={"create-form-field-item"}>
                    <label htmlFor="isConfidential">ZKP verification needed?</label>
                    <FormControl sx={{ m: 1, width: "30em" }}>
                        <Select
                            id="isConfidential"
                            name="isConfidential-disabled"
                            value={formData.isConfidential}
                            input={<OutlinedInput />}
                            disabled
                            sx={{
                                cursor: "not-allowed",
                                backgroundColor: "#f5f5f5",
                                color: "rgba(0, 0, 0, 0.6)",
                            }}
                        >
                            <MenuItem value={1}>Yes</MenuItem>
                            <MenuItem value={0}>No</MenuItem>
                            <MenuItem value={2}>Yes</MenuItem>
                        </Select>

                        <input type="hidden" name="isConfidential" value={formData.isConfidential} />
                    </FormControl>

                </div>

                <div className={"create-form-field-item"}>
                    <label htmlFor={"description"}>Description</label>
                    <textarea
                        id={"description"}
                        name={"description"}
                        placeholder={"Enter description"}
                        value={formData.description}
                        onChange={handleChange}
                    />
                </div>
            </div>

            <div className={"create-form-buttons"}>
                <CancelButton/>
                <button type={"submit"}>
                    Update context
                </button>
            </div>
        </Form>
    );
}

export default UpdateContextFieldForm;
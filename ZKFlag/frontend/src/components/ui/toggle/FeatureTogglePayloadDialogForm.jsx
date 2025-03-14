import React, { useState, useEffect } from 'react';
import { toast } from "react-toastify";
import { Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import { Form } from "react-router-dom";
import  "../../../index.css"

function FeatureTogglePayloadDialogForm({ onClose, open, defaultValueOn, defaultValueOff, submitHandler, edit }) {
    const [enabledType, setEnabledType] = useState("text");
    const [disabledType, setDisabledType] = useState("text");
    const [enabledFile, setEnabledFile] = useState(null);
    const [disabledFile, setDisabledFile] = useState(null);
    const [enabledFilename, setEnabledFilename] = useState("");
    const [disabledFilename, setDisabledFilename] = useState("");
    const [pendingDeleteFileUrls, setPendingDeleteFileUrls] = useState([]);

    useEffect(() => {
        if (edit) {
            if (isImageUrl(defaultValueOn)) {
                setEnabledType("image");
                setEnabledFilename(getFilenameFromUrl(defaultValueOn));
            } else {
                setEnabledType("text");
            }

            if (isImageUrl(defaultValueOff)) {
                setDisabledType("image");
                setDisabledFilename(getFilenameFromUrl(defaultValueOff));
            } else {
                setDisabledType("text");
            }
        }
    }, [edit, defaultValueOn, defaultValueOff]);

    const isImageUrl = (value) => typeof value === "string" && value.match(/\.(jpg|jpeg|png|gif)$/i);
    const getFilenameFromUrl = (url) => url.split('/').pop().split('?')[0];

    const uploadFile = async (file) => {
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch('/api/minio/upload', {
                method: 'POST',
                body: formData,
                credentials: "include",
            });

            if (response.ok) {
                return await response.text();
            } else {
                throw new Error('File upload failed');
            }
        } catch (error) {
            toast.error(error.message);
        }
    };

    const deleteFile = async (url) => {
        try {
            const response = await fetch(`/api/minio/delete?fileUrl=${encodeURIComponent(url)}`, {
            method: 'DELETE', credentials: "include",
        });

        if (!response.ok) {
            throw new Error("Failed to delete the file");
        }
    } catch (error) {
        toast.error(error.message);
    }
};

    const handleClose = () => {
        setPendingDeleteFileUrls([]);
        onClose();
    };

    const handleFileChange = (e, type) => {
        const file = e.target.files[0];

        if (file) {
            const isValidImage = /\.(jpg|jpeg|png|gif)$/i.test(file.name);
            if (!isValidImage) {
                toast.error("Please upload a valid image file (jpg, jpeg, png, gif).")
                return;
            }

            if (type === "enabled") {
                setEnabledFile(file);
                setEnabledFilename(file.name);
            } else {
                setDisabledFile(file);
                setDisabledFilename(file.name);
            }
        }
    };

    const handleRemoveImage = (type) => {
        if (type === "enabled") {
            setEnabledFile(null);
            setEnabledFilename("");
            if (isImageUrl(defaultValueOn)) {
                setPendingDeleteFileUrls((prev) => [...prev, defaultValueOn]);
            }
        } else {
            setDisabledFile(null);
            setDisabledFilename("");
            if (isImageUrl(defaultValueOff)) {
                setPendingDeleteFileUrls((prev) => [...prev, defaultValueOff]);
            }
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);

        let enabledValue = formData.get("valueOn");
        let disabledValue = formData.get("valueOff");

        if (enabledFile) enabledValue = await uploadFile(enabledFile);
        if (disabledFile) disabledValue = await uploadFile(disabledFile);

        if (!enabledValue && !disabledValue && !isImageUrl(defaultValueOn) && !isImageUrl(defaultValueOff)) {
            toast.error("Please provide at least one value (text or image).");
            setPendingDeleteFileUrls([])
            return;
        }
        if(!disabledValue&& !enabledValue&&!isImageUrl(defaultValueOn) && isImageUrl(defaultValueOff) &&
            pendingDeleteFileUrls.includes(defaultValueOff)){
            toast.error("Please provide at least one value (text or image).");
            setPendingDeleteFileUrls([])
            return;
        }
        if(!disabledValue && !enabledValue && isImageUrl(defaultValueOn) && !isImageUrl(defaultValueOff) &&
            pendingDeleteFileUrls.includes(defaultValueOn)){
            toast.error("Please provide at least one value (text or image).");
            setPendingDeleteFileUrls([])
            return;
        }
        if(!disabledValue && !enabledValue && isImageUrl(defaultValueOn) && isImageUrl(defaultValueOff) &&
            pendingDeleteFileUrls.includes(defaultValueOff) && pendingDeleteFileUrls.includes(defaultValueOn)){
            toast.error("Please provide at least one value (text or image).");
            setPendingDeleteFileUrls([]);
            return;
        }


        if (!enabledValue && isImageUrl(defaultValueOn) && !pendingDeleteFileUrls.includes(defaultValueOn)) {
            enabledValue = defaultValueOn;
        }

        if (!disabledValue && isImageUrl(defaultValueOff) && !pendingDeleteFileUrls.includes(defaultValueOff)) {
            disabledValue = defaultValueOff;
        }



        if (pendingDeleteFileUrls.length > 0) {
            for (const url of pendingDeleteFileUrls) {
                await deleteFile(url);
            }
            setPendingDeleteFileUrls([]);
        }

        submitHandler({ enabledValue, disabledValue });
        handleClose();
    };

    const handleTypeChange = (e, type) => {
        const newType = e.target.value;

        if (type === "enabled") {
            setEnabledType(newType);
            setEnabledFile(null);
            setEnabledFilename("");
            setPendingDeleteFileUrls((prev) =>
                newType === "text" && isImageUrl(defaultValueOn) ? [...prev, defaultValueOn] : prev
            );
        } else {
            setDisabledType(newType);
            setDisabledFile(null);
            setDisabledFilename("");
            setPendingDeleteFileUrls((prev) =>
                newType === "text" && isImageUrl(defaultValueOff) ? [...prev, defaultValueOff] : prev
            );
        }
    };


    return (
        <Dialog onClose={handleClose} open={open} style={{ width: "700px !important",
            height: "800px !important",
            overflow: "hidden !important",
            display: "flex !important",
            flexDirection: "column !important",}}>
            <DialogTitle>{edit ? "Edit payload" : "Add payload"}</DialogTitle>
            <Form method="post" onSubmit={handleSubmit}>
                <DialogContent>
                    <div className={"create-form-fields"}>
                        <div className={"create-form-field-item"}>
                            <label htmlFor={"valueOn"}>Value when toggle is enabled:</label>
                            <select
                                id={"valueOnType"}
                                name={"valueOnType"}
                                value={enabledType}
                                onChange={(e) => handleTypeChange(e, "enabled")}
                            >
                                <option value="text">Text</option>
                                <option value="image">Image</option>
                            </select>
                            {enabledType === "text" ? (
                                <input
                                    id={"valueOn"}
                                    name={"valueOn"}
                                    type={"text"}
                                    placeholder={"ON value"}
                                    defaultValue={!isImageUrl(defaultValueOn) ? defaultValueOn : ""}
                                />
                            ) : (
                                <div>
                                    <input
                                        id={"enabledFile"}
                                        name={"enabledFile"}
                                        type={"file"}
                                        accept=".jpg, .jpeg, .png"
                                        onChange={(e) => handleFileChange(e, "enabled")}
                                    />
                                    {enabledFilename && <p>Current file: {enabledFilename}</p>}
                                    {enabledFilename && (
                                        <button type="button" onClick={() => handleRemoveImage("enabled")}>Remove</button>
                                    )}
                                </div>
                            )}
                        </div>

                        <div className={"create-form-field-item"}>
                            <label htmlFor={"valueOff"}>Value when toggle is disabled:</label>
                            <select
                                id={"valueOffType"}
                                name={"valueOffType"}
                                value={disabledType}
                                onChange={(e) => handleTypeChange(e, "disabled")}
                            >
                                <option value="text">Text</option>
                                <option value="image">Image</option>
                            </select>
                            {disabledType === "text" ? (
                                <input
                                    id={"valueOff"}
                                    name={"valueOff"}
                                    type={"text"}
                                    placeholder={"OFF value"}
                                    defaultValue={!isImageUrl(defaultValueOff) ? defaultValueOff : ""}
                                />
                            ) : (
                                <div>
                                    <input
                                        id={"disabledFile"}
                                        name={"disabledFile"}
                                        type={"file"}
                                        accept=".jpg, .jpeg, .png"
                                        onChange={(e) => handleFileChange(e, "disabled")}
                                    />
                                    {disabledFilename && <p>Current file: {disabledFilename}</p>}
                                    {disabledFilename && (
                                        <button type="button" onClick={() => handleRemoveImage("disabled")}>Remove</button>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                </DialogContent>
                <DialogActions>
                    <button className="schedule-dialog-cancel-btn" onClick={(event) => { event.preventDefault(); handleClose(); }}>
                        Cancel
                    </button>
                    <button className="schedule-dialog-save-btn" type="submit">Save</button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default FeatureTogglePayloadDialogForm;

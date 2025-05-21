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
    Select,
    TextField
} from "@mui/material";
import OperatorField from "./OperatorField.jsx";
import ConstraintValuesList from "./ConstraintValuesList.jsx";
import { Form } from "react-router-dom";
import { toast } from "react-toastify";

const coverageOptions = [
    { label: "City", value: 0 },
    { label: "Country", value: 1 },
    { label: "Continent", value: 2 }
];

function FeatureToggleAddConstraintDialog({
                                              onClose, open, contextFields, submitHandler, pContext,
                                              pOperator, pValues, edit, instanceId, isConfidential
                                          }) {
    const safeContextFields = Array.isArray(contextFields) ? contextFields : [];
    const [context, setContext] = useState(pContext || '');
    const [values, setValues] = useState([]);
    const [latitude, setLatitude] = useState('');
    const [longitude, setLongitude] = useState('');
    const [radius, setRadius] = useState("0");
    const [isLocation, setIsLocation] = useState(false);

    useEffect(() => {
        if ( edit &&  pValues) {

            if (isConfidential===2 && pValues.length >= 3) {
                const [rad, lat, lng] = pValues;

                console.log("lat:", lat, " lng: ", lng, " rad:", rad);

                setLatitude(lat === "0" ? '' : lat);
                setLongitude(lng === "0" ? '' : lng)
                setRadius(rad)
            }
        }
    }, [ pValues,open]);


    useEffect(() => {
        setIsLocation(isConfidential === 2);
    }, [isConfidential]);


    useEffect(() => {
        if (pContext) setContext(pContext.id);
    }, [pContext]);

    useEffect(() => {
        if (pValues) {
                setValues(pValues);
            }
    }, [pValues, instanceId]);


    const handleClose = () => {
        onClose();
            setContext('');
            setValues([]);
            setLatitude('');
            setLongitude('');
            setRadius(0);

    };

    const handleChange = (event) => {
        setContext(event.target.value || '');
        const selected = contextFields.find((c) => c.id === event.target.value);
        setIsLocation(selected?.isConfidential === 2);
        setValues([]);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        const contextValue = formData.get("context");
        const operator = formData.get("operator");

        if (isLocation) {
            const lat = parseFloat(latitude);
            const lng = parseFloat(longitude);

            if (
                isNaN(lat) || isNaN(lng) ||
                lat < -90 || lat > 90 ||
                lng < -180 || lng > 180
            ) {
                toast.error("Latitude must be between -90 and 90, and longitude between -180 and 180.");
                return;
            }
        }

        if (instanceId == null) {
            const elementWithId = contextFields.find((obj) => obj.id === contextValue);
            if (!contextValue || (isLocation
                ? (!longitude || !latitude )
                : values.length === 0)) {
                toast.error("Please fill all required fields!");
                return;
            }

            const finalValues = isLocation
                ? [radius, latitude,longitude ]
                : values;

            submitHandler({
                contextName: elementWithId?.name,
                operator,
                values: finalValues,
            }, elementWithId?.isConfidential);
        } else {
            const finalValues = isLocation
                ? [radius,latitude,longitude]
                : values;

            submitHandler({
                contextName: pContext.name,
                operator: pOperator,
                values: finalValues,
            }, pContext.isConfidential);
        }

        handleClose();
    };

    const contextFieldsDropDown = Array.isArray(contextFields)
        ? contextFields.map((el) => (
            <MenuItem value={el.id} key={el.id}>{el.name}</MenuItem>
        ))
        : [];

    return (
        <Dialog onClose={handleClose} open={open}>
            <DialogTitle>{edit ? "Edit constraint" : "Add new constraint"}</DialogTitle>
            <Form method="post" onSubmit={handleSubmit}>
                <DialogContent>
                    {instanceId == null && (
                        <FormControl sx={{ m: 1, width: 320 }}>
                            <InputLabel id="context-select-label">Context</InputLabel>
                            <Select
                                labelId="context-select-label"
                                id="context-select"
                                name="context"
                                value={context}
                                onChange={handleChange}
                                input={<OutlinedInput label="Context" />}
                            >
                                {contextFieldsDropDown.length === 0 ?
                                    <MenuItem disabled>No context field</MenuItem>
                                    : contextFieldsDropDown}
                            </Select>
                        </FormControl>
                    )}

                    {
                        isLocation ? (
                            <>
                            <div className="constraint-form-operator">
                                <label className={"constraint-form-operator-label selected" }>
                                    <input
                                        type="radio"
                                        name="operator"
                                        value="IN"
                                        className="constraint-form-operator-input"
                                        checked={true}
                                    />
                                    IN
                                </label>
                            </div>
                                <br/>

                                <TextField
                                    label="Latitude"
                                    type="number"
                                    fullWidth={false} 
                                    sx={{ width: "150px", m: 1 }}
                                    value={latitude}
                                    onChange={(e) => setLatitude(e.target.value)}
                                    inputProps={{ step: "0.0001", min: -90, max: 90 }}
                                    required
                                />

                                <TextField
                                    label="Longitude"
                                    type="number"
                                    fullWidth={false}
                                    sx={{ width: "150px", m: 1 }}
                                    value={longitude}
                                    onChange={(e) => setLongitude(e.target.value)}
                                    inputProps={{ step: "0.0001", min: -180, max: 180 }}
                                    required
                                />

                                {instanceId == null &&
                                <FormControl sx={{ m: 1, width: 320 }}>
                                    <InputLabel id="radius-label">Coverage</InputLabel>
                                    <Select
                                        labelId="radius-label"
                                        id="radius"
                                        value={radius}
                                        onChange={(e) => setRadius(e.target.value)}
                                        input={<OutlinedInput label="Coverage" />}
                                    >
                                        {coverageOptions.map(option => (
                                            <MenuItem key={option.value} value={option.value}>
                                                {option.label}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                                }
                            </>
                        ) : (

                            <>
                                {instanceId == null && <OperatorField defaultOperator={pOperator} />}
                                {/*<ConstraintValuesList*/}
                                {/*    values={values}*/}
                                {/*    setValues={setValues}*/}
                                {/*/>*/}
                                <ConstraintValuesList
                                    values={values}
                                    setValues={setValues}
                                    onValueChange={(val) => {
                                        if (val === '') return;
                                        if (values.includes(val)) {
                                            toast.error("Value already exists!");
                                            return;
                                        }
                                        if (values.length >= 1 && !isLocation) {
                                            toast.error("Only one value allowed.");
                                            return;
                                        }
                                        setValues([val]);
                                    }}
                                />

                            </>
                    )}
                </DialogContent>
                <DialogActions>
                    <button className="reverse-btn" onClick={(e) => { e.preventDefault(); handleClose(); }}>
                        Cancel
                    </button>
                    <button type="submit">
                        {edit ? "Update" : "Save"}
                    </button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default FeatureToggleAddConstraintDialog;

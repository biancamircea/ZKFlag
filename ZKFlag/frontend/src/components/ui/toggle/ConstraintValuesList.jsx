import React, { useState,useEffect } from 'react';
import { toast } from "react-toastify";
import {Info} from "react-feather";
import Tooltip from "@mui/material/Tooltip";

function ConstraintValuesList({ values, setValues, instanceId, onValueChange }) {
    const [inputValue, setInputValue] = useState('');

    useEffect(() => {
        if (values && values.length > 0) {
            setInputValue(values[0]);
        } else {
            setInputValue('');
        }
    }, [values]);

    const handleChange = (e) => {
        const val = e.target.value;
        setInputValue(val);
        setValues([val]);
    };

    // const valuesEl = values.map((value, index) => (
    //     <div key={index} className="constraint-value-container">
    //         <span className="value">{value}</span>
    //     </div>
    // ));

    return (
        <div className="constraint-values-wrapper">
            <div className="constraint-values-input-wrapper">
                <input
                    type="text"
                    placeholder="Insert value"
                    value={inputValue}
                    onChange={handleChange}
                />
                <Tooltip
                    title={"Only one value allowed (string or number). For \"Greater Than\"/\"Less Than\", use positive numbers."}
                    arrow
                    style={{ display: "flex", alignItems: "center" }}
                >
                    <span style={{ display: "flex", alignItems: "center" }}>
                        <Info className="info-icon" size={25} />
                    </span>
                </Tooltip>
            </div>
        </div>
    );
}

export default ConstraintValuesList;

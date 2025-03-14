import React, {useState} from 'react';
import Switch from "@mui/material/Switch";

function SimpleSwitch({checked, handleEnable, handleDisable}) {
    const [isActive, setIsActive] = useState(checked)
    function handleChange(event) {
        if(event.target.checked){
            handleEnable()
        } else {
            handleDisable()
        }
        setIsActive(prevState => !prevState)
    }

    return (
        <>
            <Switch
                onClick={event => event.stopPropagation()}
                checked={isActive}
                onChange={handleChange}
            />
        </>
    );
}

export default SimpleSwitch;
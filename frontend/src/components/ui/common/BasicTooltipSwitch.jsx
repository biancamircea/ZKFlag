import React, {useState} from 'react';
import Switch from '@mui/material/Switch';
import Tooltip from "@mui/material/Tooltip";
function BasicTooltipSwitch({checked, handleEnable, handleDisable}) {
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
            <Tooltip title={!isActive ? "Enable environment" : "Disable environment"} arrow>
                <Switch
                    checked={isActive}
                    onChange={handleChange}
                />
            </Tooltip>
        </>
    );
}

export default BasicTooltipSwitch;
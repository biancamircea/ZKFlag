import React, {useState} from 'react';
import Switch from "@mui/material/Switch";
import Tooltip from "@mui/material/Tooltip";

function TooltipSwitch({checked, handleEnable, handleDisable, environmentName}) {
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
            <Tooltip title={isActive ? `Disable in ${environmentName}` : `Enable in ${environmentName}`} arrow>
                <Switch
                    onClick={event => event.stopPropagation()}
                    checked={isActive}
                    onChange={handleChange}
                />
            </Tooltip>
        </>
    );
}

export default TooltipSwitch;
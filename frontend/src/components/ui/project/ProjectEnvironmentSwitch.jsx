import React, {useState} from 'react';
import Tooltip from "@mui/material/Tooltip";
import Switch from "@mui/material/Switch";
import DialogDisableEnvironment from "../DialogDisableEnvironment.jsx";

function ProjectEnvironmentSwitch({checked, handleEnable, handleDisable, resourceName, enabledToggleCount}) {
    const [isActive, setIsActive] = useState(checked)
    const [open, setOpen] = useState(false);
    const handleClickOpen = () => {
        if(enabledToggleCount !== 0){
            setOpen(true);
        } else {
            handleDisable()
            setIsActive(prevState => !prevState)
        }
    };

    function handleChange(event) {
        if(event.target.checked){
            handleEnable()
            setIsActive(prevState => !prevState)
        } else {
            handleClickOpen()
        }
    }

    return (
        <>
            <Tooltip title={!isActive ? "Enable environment" : "Disable environment"} arrow>
                <Switch
                    checked={isActive}
                    onChange={handleChange}
                />
            </Tooltip>
            <DialogDisableEnvironment
                deleteHandler={handleDisable}
                resourceName={resourceName}
                open={open}
                setOpen={setOpen}
                setIsActive={setIsActive}
                enabledToggleCount={enabledToggleCount}
            />
        </>
    );
}

export default ProjectEnvironmentSwitch;
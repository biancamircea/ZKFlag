import React, {useState} from 'react';
import Switch from "react-switch";
import DialogDisableEnvironment from "../DialogDisableEnvironment.jsx";
function CustomSwitch({checked, handleEnable, handleDisable, resourceName, enabledToggleCount}) {

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
    function handleChange(checked, event, id) {
        if(checked){
            handleEnable()
            setIsActive(prevState => !prevState)
        } else {
            handleClickOpen()
            // handleDisable()
        }
    }

    return (
        <div className={"environments-switch-wrapper"} onClick={(event) => event.stopPropagation()}>
            <Switch
                checked={isActive}
                onChange={handleChange}
                className={"react-switch"}

                onColor="#86d3ff"
                onHandleColor="#2693e6"
                handleDiameter={25}
                uncheckedIcon={false}
                checkedIcon={false}
                boxShadow="0px 1px 5px rgba(0, 0, 0, 0.6)"
                activeBoxShadow="0px 0px 1px 10px rgba(0, 0, 0, 0.2)"
                height={16}
                width={45}
            />
            <DialogDisableEnvironment
                deleteHandler={handleDisable}
                resourceName={resourceName}
                open={open}
                setOpen={setOpen}
                setIsActive={setIsActive}
                enabledToggleCount={enabledToggleCount}
            />
        </div>
    );
}

export default CustomSwitch;
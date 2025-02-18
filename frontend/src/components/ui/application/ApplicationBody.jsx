import React, {useState} from 'react';
import ApplicationNav from "../../nav/ApplicationNav.jsx";
import {Outlet} from "react-router-dom";

function ApplicationBody({applicationProp}) {
    const [application, setApplication] = useState(applicationProp)
    const updateHandler = (updatedApplication) => {
        setApplication(updatedApplication);
    };

    function deleteInstanceHandler(id){
        // Filter out the instance you want to delete based on its id
        const updatedInstances = application.instances.filter((instance) => instance.id !== id);

        // Update the state with the filtered instances
        setApplication({
            ...application,
            instances: updatedInstances
        });
    }

    function deleteAllInstancesHandler(){
        // Update the state with the filtered instances
        setApplication({
            ...application,
            instances: []
        });
    }

    return (
        <div className={"body-container"}>
            <div>
                Created at: <span className={"bold-text"}> { new Date(application.createdAt).toLocaleDateString("ro")}</span>
            </div>
            <ApplicationNav/>
            <Outlet context={{application, updateHandler, deleteInstanceHandler, deleteAllInstancesHandler}}/>
        </div>
    );
}

export default ApplicationBody;
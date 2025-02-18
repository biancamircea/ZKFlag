import React, {useState} from 'react';
import ApplicationInstancesListItem from "./ApplicationInstancesListItem.jsx";
import {deleteApplicationInstance} from "../../../api/applicationApi.js";
import {toast} from "react-toastify";
import {useParams} from "react-router-dom";

function ApplicationInstancesList({instances, deleteHandler}) {
    const { appId } = useParams();
    async function deleteInstance(instanceId){
        const response = await deleteApplicationInstance(appId, instanceId)
        if(response){
            toast.success("Instance deleted.")
            deleteHandler(instanceId)
        } else {
            toast.error("Operation failed.")
        }
    }

    const instancesListItemEl = instances.map(el => {
        return (
            <ApplicationInstancesListItem
                key={el.id}
                name={el.nameId}
                startedAt={el.startedAt}
                deleteHandler={() => deleteInstance(el.id)}
            />
        )
    })

    return (
        <>
            {
                instancesListItemEl
            }
        </>
    );
}

export default ApplicationInstancesList;
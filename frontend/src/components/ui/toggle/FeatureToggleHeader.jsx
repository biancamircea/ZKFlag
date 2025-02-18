import React from 'react';
import FeatureTogglePath from "./FeatureTogglePath.jsx";
import FeatureToggleInstancePath from "./FeatureToggleInstancePath.jsx";
import DeleteIcon from "../common/DeleteIcon.jsx";
import {useNavigate, useParams} from "react-router-dom";
import {toast} from "react-toastify";
import {addTagToToggle, deleteToggleFromProject} from "../../../api/featureToggleApi.js";
import FeatureToggleNav from "../../nav/FeatureToggleNav.jsx";
import TagIcon from "../common/TagIcon.jsx";
import TagDialog from "./TagDialog.jsx";
import FeatureToggleInstanceNav from "../../nav/FeatureToggleInstanceNav.jsx";

function FeatureToggleHeader({projectName, toggleName, tags, featureTags, addFeatureTag,instanceName}) {
    const { projectId, featureId } = useParams();
    const navigate = useNavigate();

    const [open, setOpen] = React.useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };
    async function handleClose(value){
        if(value){
            // console.log(value)
            const response = addTagToToggle(projectId, featureId, value)
            if(response){
                addFeatureTag(value)
                toast.success("Tag added.");
            } else {
                toast.error("Operation failed.");
            }
        }
        setOpen(false);
    };

    async function deleteHandler(id){
        const res = await deleteToggleFromProject(projectId, id);
        if (res) {
            toast.success("Feature toggle deleted.");
            navigate("/projects")
        } else {
            toast.error("Operation failed.");
        }
    }

    const availabeTags = tags.filter((item) => {
        return !featureTags?.some((otherItem) => {
            return item.id === otherItem.id;
        });
    });

    return (
        <>
            {instanceName ? (
                <FeatureToggleInstancePath
                    projectName={projectName}
                    instanceName={instanceName}
                    toggleName={toggleName}
                />
            ) : (
                <FeatureTogglePath
                    projectName={projectName}
                    toggleName={toggleName}
                />
            )}
            <div className={"project-header-container"}>
                <h2>{toggleName}</h2>
                {!instanceName && (
                <div className={"list-page-header-functions"}>
                    <TagIcon
                        handleClick={handleClickOpen}
                    />
                    <TagDialog
                        open={open}
                        onClose={handleClose}
                        tags={availabeTags}
                    />
                    <DeleteIcon
                        resource={"Feature toggle"}
                        resourceName={toggleName}
                        deleteHandler={() => deleteHandler(featureId)}
                    />
                </div>
                )}
            </div>
            {instanceName ? (<FeatureToggleInstanceNav/>): (
            <FeatureToggleNav/>)}
        </>
    );
}

export default FeatureToggleHeader;
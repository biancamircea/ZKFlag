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
import {getAllInstancesFromProject} from "../../../api/instanceApi.js";;
import {getToggleEnvironments} from "../../../api/instanceApi.js";
import CONFIG from "../../../Config.jsx";


function FeatureToggleHeader({projectName, toggleName, tags, featureTags, addFeatureTag,instanceName}) {
    const { projectId, featureId } = useParams();
    const navigate = useNavigate();

    const [open, setOpen] = React.useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };
    async function handleClose(value){
        if(value){
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

    const isImageUrl = (value) => {
        return value && (value.endsWith('.jpg') || value.endsWith('.jpeg') || value.endsWith('.png') || value.endsWith('.gif'));
    };

    const deleteFile = async (fileUrl) => {
        try {
            const response = await fetch(`/api/minio/delete?fileUrl=${encodeURIComponent(fileUrl)}`, {
                method: "DELETE",
                credentials: "include",
            });

            if (!response.ok) {
                throw new Error("File deletion failed");
            }

        } catch (error) {
            toast.error(error.message);
        }
    };

    async function deleteHandler(id) {
        try {
            const instances = await getAllInstancesFromProject(projectId);

            let imageUrls = [];

            for (const instance of instances) {
                const environments = await getToggleEnvironments(instance.id, id);
                for (const env of environments) {
                    if (env.enabledValue && isImageUrl(env.enabledValue)) {
                        imageUrls.push(env.enabledValue);
                    }
                    if (env.disabledValue && isImageUrl(env.disabledValue)) {
                        imageUrls.push(env.disabledValue);
                    }
                }
            }

            for (const imageUrl of imageUrls) {
                await deleteFile(imageUrl);
            }

            const res = await deleteToggleFromProject(projectId, id);
            if (res) {
                toast.success("Feature toggle deleted.");
                navigate("/projects");
            } else {
                throw new Error("Operation failed.");
            }
        } catch (error) {
            toast.error(error.message);
        }
    }

    const availabeTags = Array.isArray(tags) ? tags.filter((item) => {
        return !featureTags?.some((otherItem) => item.id === otherItem.id);
    }) : [];


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
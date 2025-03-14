import ListPageHeader from "../common/ListPageHeader.jsx";
import React, { useEffect, useState,useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import EditIcon from "../common/EditIcon.jsx";
import DeleteIcon from "../common/DeleteIcon.jsx";
import EmptySearchResult from "../common/EmptySearchResult.jsx";
import FeaturesListHeader from "./FeaturesListHeader.jsx";
import { toast } from "react-toastify";
import { deleteToggleFromProject } from "../../../api/featureToggleApi.js";
import EmptyList from "../common/EmptyList.jsx";
import FeatureTag from "../common/FeatureTag.jsx";
import {getToggleEnvironments, getAllInstancesFromProject} from "../../../api/instanceApi.js";
import CONFIG from "../../../Config.jsx";

function ProjectFeatureToggles({ toggles }) {
    const navigate = useNavigate();
    const [togglesList, setTogglesList] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const { projectId } = useParams();
    const [nameWidths, setNameWidths] = useState({});
    const nameRefs = useRef({});

    useEffect(() => {
        const widths = {};
        Object.keys(nameRefs.current).forEach(key => {
            widths[key] = nameRefs.current[key].offsetWidth;
        });
        setNameWidths(widths);
    }, [toggles])


    function handleSearch(query) {
        setSearchQuery(query);
    }

    useEffect(() => {
        setTogglesList(toggles);
    }, [toggles]);

    async function deleteToggle(id) {
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
                setTogglesList(prevToggles => prevToggles.filter(el => el.id !== id));
            } else {
                throw new Error("Operation failed.");
            }
        } catch (error) {
            toast.error(error.message);
        }
    }


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


    function handleItemClick(id) {
        navigate(`features/${id}`);
    }

    const filteredToggles = togglesList?.filter((el) =>
        el.name.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const sortedToggles = filteredToggles?.sort((a, b) => {
        if (a.id < b.id) return -1;
        if (a.id > b.id) return 1;
        return 0;
    });

    const togglesEl = sortedToggles?.map(el => {
        const tagsEl = el.tags.map((el2) => {
            return (
                <FeatureTag
                    key={el2.id}
                    label={el2.labelName}
                    color={el2.color}
                />
            );
        });

        return (
            <div
                className={"feature-list-item item-body"}
                key={el.id}
                onClick={() => handleItemClick(el.id)}
            >
                <div className={"feature-list-item-wrapper"}>
                    <div className={"feature-list-item-left item-body"}>
                        <p id={"feature-name"}>
                            {el.name}
                        </p>
                        <p style={{ marginLeft: `${500 - el.name.length*7.8}px` }}>{el.createdAt ? new Date(el.createdAt).toLocaleDateString("ro") : "null"}</p>
                    </div>
                    <div className={"feature-list-item-left tags"}>
                        {tagsEl}
                    </div>
                </div>
                <div className={"feature-list-item-right"} style={{marginLeft: "750px"}}>
                    <div className={"feature-list-item-right-actions"}>
                        <EditIcon
                            id={el.id}
                            directLink={`/projects/${projectId}/features/edit/${el.id}`}
                        />
                        <DeleteIcon
                            resource={"Toggle"}
                            resourceName={el.name}
                            deleteHandler={() => deleteToggle(el.id)}
                        />
                    </div>
                </div>
            </div>
        );
    });

    function renderResult() {
        if (togglesList?.length === 0) {
            return (
                <EmptyList
                    resource={"feature toggle"}
                />
            );
        } else {
            if (togglesEl?.length === 0) {
                return (
                    <EmptySearchResult
                        resource={"feature toggle"}
                        searchValue={searchQuery}
                    />
                );
            } else {
                return togglesEl;
            }
        }
    }

    return (
        <>
            <ListPageHeader
                title={"Feature toggles"}
                buttonText={"New feature"}
                hasButton={true}
                auxiliaryPath={"features"}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className={"list-container"}>
                <FeaturesListHeader />
                {renderResult()}
            </div>
        </>
    );
}

export default ProjectFeatureToggles;

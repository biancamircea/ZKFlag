import ListPageHeader from "../common/ListPageHeader.jsx";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import EditIcon from "../common/EditIcon.jsx";
import DeleteIcon from "../common/DeleteIcon.jsx";
import EmptySearchResult from "../common/EmptySearchResult.jsx";
import FeaturesListHeader from "../project/FeaturesListHeader.jsx";
import { toast } from "react-toastify";
import { deleteToggleFromProject } from "../../../api/featureToggleApi.js";
import EmptyList from "../common/EmptyList.jsx";
import FeatureTag from "../common/FeatureTag.jsx";
import FeaturesListInstanceHeader from "./FeatureListInstanceHeader.jsx";

function InstanceFeatureToggles({ toggles }) {
    const navigate = useNavigate();
    const [togglesList, setTogglesList] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const { projectId } = useParams();

    function handleSearch(query) {
        setSearchQuery(query);
    }

    useEffect(() => {
        setTogglesList(toggles);
    }, [toggles]);

    async function deleteToggle(id) {
        const res = await deleteToggleFromProject(projectId, id);
        if (res) {
            toast.success("Feature toggle deleted.");
            setTogglesList(prevToggles => prevToggles.filter(el => el.id !== id));
        } else {
            toast.error("Operation failed.");
        }
    }

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
                className={"instances-list list-item item-body"} key={el.id} style={{display:"flex", justifyContent:"space-between"}}
                onClick={() => handleItemClick(el.id)}
            >
                    <div className={"instances-list-item-name"}>
                        <p id={"feature-name"}>
                            {el.name}
                        </p>
                    </div>

                    <div className={"context-fields list-item actions"}>
                        <p style={{display:"flex",marginLeft:"300px"}}> {el.createdAt ? new Date(el.createdAt).toLocaleDateString("ro") : "null"}</p>
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
                <FeaturesListInstanceHeader />
                {renderResult()}
            </div>
        </>
    );
}

export default InstanceFeatureToggles;

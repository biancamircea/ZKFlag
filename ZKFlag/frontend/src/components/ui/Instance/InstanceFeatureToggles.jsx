import ListPageHeader from "../common/ListPageHeader.jsx";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import EmptySearchResult from "../common/EmptySearchResult.jsx";
import EmptyList from "../common/EmptyList.jsx";
import FeatureTag from "../common/FeatureTag.jsx";
import FeaturesListInstanceHeader from "./FeatureListInstanceHeader.jsx";

function InstanceFeatureToggles({ toggles }) {
    const navigate = useNavigate();
    const [togglesList, setTogglesList] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');

    function handleSearch(query) {
        setSearchQuery(query);
    }

    useEffect(() => {
        setTogglesList(toggles);
    }, [toggles]);

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
                className={"instances-list list-item item-body"} key={el.id}
                onClick={() => handleItemClick(el.id)}
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    width: "100%"
                }}
            >
                    <div style={{flex: "1.5", display: "flex", justifyContent: "flex-start"}}>
                        <p id={"feature-name"}>
                            {el.name}
                        </p>
                    </div>

                    <div  style={{flex: "1.5",display:"flex",justifyContent: "flex-end"}}>
                        <p style={{marginRight:"20px"}}> {el.createdAt ? new Date(el.createdAt).toLocaleDateString("ro") : "null"}</p>
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
                buttonText={""}
                hasButton={false}
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

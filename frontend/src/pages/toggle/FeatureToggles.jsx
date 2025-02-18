import React, {Suspense, useState} from 'react';
import {Await, defer, useLoaderData} from "react-router-dom";
import {getToggles} from "../../api/featureToggleApi.js";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import FeatureTogglesList from "../../components/ui/toggle/FeatureTogglesList.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
import FeatureTogglesListPageHeader from "../../components/ui/toggle/FeatureTogglesListPageHeader.jsx";

export function loader(){
    return defer({ toggles: getToggles() })
}
function FeatureToggles(props) {
    const loaderDataPromise = useLoaderData()
    const [searchQuery, setSearchQuery] = useState('');
    const [tag, setTag] = useState('');



    function handleSearch(query) {
        setSearchQuery(query);
    }
    const handleFilter = (event) => {
        setTag(Number(event.target.value) || '');
    };

    function extractTags(toggles){
        let tags = [];
        const uniqueTags = [];
        const uniqueTagIds = {};

        toggles.forEach(element => {
            tags = [...tags, ...element.tags];
        });

        tags.forEach(tag => {
            if (!uniqueTagIds[tag.id]) {
                uniqueTagIds[tag.id] = true;
                uniqueTags.push(tag);
            }
        });
        return uniqueTags
    }

    function render(response){
        let tagFilteredToggles = response.toggles
        if(tag !== ''){
            tagFilteredToggles = response.toggles.filter(toggle => toggle.tags.some(t => t.id === Number(tag)));
        }

        const filteredToggles = tagFilteredToggles.filter((el) =>
            el.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        function renderList() {
            if(response.toggles.length === 0){
                return (
                    <EmptyList resource={"feature toggle"}/>
                )
            } else {
                if(filteredToggles.length === 0){
                    return (
                        <EmptySearchResult resource={"feature toggle"} searchValue={searchQuery} />
                    )
                } else {
                    return (
                        <FeatureTogglesList
                            toggles={filteredToggles}
                        />
                    )
                }
            }
        }

        return (
            <>
                <FeatureTogglesListPageHeader
                    title={`Feature toggles (${filteredToggles.length})`}
                    searchQuery={searchQuery}
                    handleSearch={handleSearch}
                    tags={extractTags(response.toggles)}
                    tag={tag}
                    handleFilter={handleFilter}
                />
                <div className={"list-container"}>
                    <div className={"list-item item-header features"}>
                        <p style={{textAlign: 'left'}}>Name</p>
                        <p style={{textAlign: 'left'}}>Tags</p>
                        <p>Created</p>
                        <p>Project</p>
                    </div>
                    {
                        renderList()
                    }
                </div>
            </>
        )
    }

    return (
        <>
            <Suspense fallback={<LoadingBanner/>}>
                <Await resolve={loaderDataPromise.toggles}>
                    {
                        render
                    }
                </Await>
            </Suspense>
        </>
    );
}

export default FeatureToggles;
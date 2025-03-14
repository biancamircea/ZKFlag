import React, {Suspense, useState,useEffect} from 'react';
import {Await, defer, useLoaderData} from "react-router-dom";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import FeatureTogglesList from "../../components/ui/toggle/FeatureTogglesList.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
import FeatureTogglesListPageHeader from "../../components/ui/toggle/FeatureTogglesListPageHeader.jsx";
import {getTogglesFromProject} from "../../api/featureToggleApi.js";
import {getProjectsForProjectAdmin,fetchUser} from "../../api/userApi.js";

export async function loader({params}){
    const user=await fetchUser();
    if (!user || !user.id) {
        throw new Error("User ID is undefined.");
    }
    return defer({ projects: getProjectsForProjectAdmin(user.id) })
}


function FeatureToggles(props) {
    const loaderDataPromise = useLoaderData()
    const [searchQuery, setSearchQuery] = useState('');
    const [tag, setTag] = useState('');
    const [toggles, setToggles] = useState([]);


    useEffect(() => {
        async function fetchToggles() {
            try {
                const projects = await loaderDataPromise.projects;
                if (projects) {
                    const togglePromises = projects.map(project => getTogglesFromProject(project.id));
                    const resolvedToggles = await Promise.all(togglePromises);

                    const allToggles = resolvedToggles.flatMap(obj => obj.toggles);
                    setToggles(allToggles);

                }
            } catch (error) {
                console.error("Error loading feature toggles:", error);
            }
        }

        fetchToggles();
    }, [loaderDataPromise]);





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

    function render() {
        let tagFilteredToggles = toggles || [];

        if(tag !== ''){
            tagFilteredToggles = toggles.filter(toggle => toggle.tags.some(t => t.id === Number(tag)));
        }

        const filteredToggles = tagFilteredToggles.filter((el) =>
            el.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        function renderList() {
            if(toggles.length === 0){
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
                    tags={extractTags(toggles)}
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
                <Await resolve={toggles}>
                    {
                        render
                    }
                </Await>
            </Suspense>
        </>
    );
}

export default FeatureToggles;
import React, {Suspense, useEffect, useState} from 'react';
import ListPageHeader from "../common/ListPageHeader.jsx";
import ProjectApiTokensHeader from "./ProjectApiTokensHeader.jsx";
import {Await, defer, useLoaderData, useParams} from "react-router-dom";
import {getInstanceApiTokens} from "../../../api/instanceApi.js";
import LoadingBanner from "../common/LoadingBanner.jsx";
import ProjectApiTokensList from "./ProjectApiTokensList.jsx";
import {getActiveEnvironmentsForInstance} from "../../../api/environmentApi.js";
export function loader({params}){

    return defer({
        apiTokens: getInstanceApiTokens(params.instanceId) ,
    })
}
function ProjectApiTokens(props) {
    const loaderDataPromise = useLoaderData()
    const [searchQuery, setSearchQuery] = useState('');
    const [environments, setEnvironments] = useState([])
    const {instanceId} = useParams();
    function handleSearch(query) {
        setSearchQuery(query);
    }


    useEffect(() => {
        async function fetchEnvironments() {
            try {
                const data = await getActiveEnvironmentsForInstance(instanceId)
                setEnvironments(data);
            } catch (error) {
                console.error("Error fetching environments:", error);
            }
        }

        fetchEnvironments();
    }, [instanceId]);

    function render(response){
        return (
            <ProjectApiTokensList
                apiTokens={response.tokens}
                searchQuery={searchQuery}
                environments={environments}
            />
        )
    }

    return (
        <>
            <ListPageHeader
                title={"Api Tokens (API Keys)"}
                buttonText={"New token"}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />

            <div className={"list-container"}>
                <ProjectApiTokensHeader/>
                <Suspense fallback={<LoadingBanner/>}>
                    <Await resolve={loaderDataPromise.apiTokens}>
                        {
                            render
                        }
                    </Await>
                </Suspense>
            </div>

        </>
    );
}

export default ProjectApiTokens;
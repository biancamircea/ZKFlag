import React, {Suspense} from 'react';
import ProjectFeatureToggles from "../../components/ui/project/ProjectFeatureToggles.jsx";
import MetadataCard from "../../components/ui/project/MetadataCard.jsx";
import MembersCard from "../../components/ui/project/MembersCard.jsx";
import {Await, defer, useLoaderData, useParams} from "react-router-dom";
import {getProjectOverview} from "../../api/projectApi.js";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";

export function loader({ params }){
    return defer({ project: getProjectOverview(params.projectId) })
}

function ProjectOverview(props) {
    const loaderDataPromise = useLoaderData()
    const {projectId} = useParams()

    function renderProjectOverview(response){
        return (
            <>
                <div className={"project-overview-wrapper"} key={response.id}>
                    <div className={"project-overview-section-left"}>
                        <MetadataCard
                            description={response.description}/>
                        <MembersCard
                            membersCount={response.members}
                            projectId={projectId}
                        />
                    </div>
                    <div className={"project-overview-section-right"}>
                        <ProjectFeatureToggles
                            toggles={response.toggles}
                        />
                    </div>
                </div>
            </>
        );
    }


    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.project}>
                {
                    renderProjectOverview
                }
            </Await>
        </Suspense>
    );
}

export default ProjectOverview;
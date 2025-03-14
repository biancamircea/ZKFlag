import React, {Suspense} from 'react';
import InstanceFeatureToggles from "../../components/ui/Instance/InstanceFeatureToggles.jsx";
import MetadataCard from "../../components/ui/project/MetadataCard.jsx";
import MembersCard from "../../components/ui/project/MembersCard.jsx";
import {Await, defer, useLoaderData, useParams} from "react-router-dom";
import {getInstanceOverview} from "../../api/instanceApi.js";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";


export function loader({ params }){
    return defer({ instance: getInstanceOverview(params.instanceId) })
}

function InstanceOverview(props) {
    const loaderDataPromise = useLoaderData()
    const {instanceId,projectId} = useParams()

    function renderInstanceOverview(response){
        return (
            <>
                <div className={"project-overview-wrapper"} key={response.id}>
                    <div className={"project-overview-section-left"}>
                        <MetadataCard
                            description={""}
                            instanceName={response.name}
                        />
                        <MembersCard
                            membersCount={response.members}
                            projectId={projectId}
                            instanceId={instanceId}
                        />
                    </div>
                    <div className={"project-overview-section-right"}>
                        <InstanceFeatureToggles
                            toggles={response.toggles}
                        />
                    </div>
                </div>
            </>
        );
    }


    return (
        <Suspense fallback={<LoadingBanner/>}>
            <Await resolve={loaderDataPromise.instance}>
                {
                    renderInstanceOverview
                }
            </Await>
        </Suspense>
    );
}

export default InstanceOverview;
import React, {Suspense, useState} from 'react';
import ListPageHeader from "../common/ListPageHeader.jsx";
import {Await, defer, useLoaderData, useParams} from "react-router-dom";
import {toast} from "react-toastify";
import {
    getAllInstancesFromProject,
    getToggleEnvironments,
    toggleEnvironmentInInstance
} from "../../../api/instanceApi.js";
import ProjectEnvironmentSwitch from "../project/ProjectEnvironmentSwitch.jsx";
import EmptyList from "../common/EmptyList.jsx";
import EmptySearchResult from "../common/EmptySearchResult.jsx";
import LoadingBanner from "../common/LoadingBanner.jsx";
import {
    getActiveEnvironmentsForInstance,
} from "../../../api/environmentApi.js";

export function loader({ params }) {

    const environmentsPromise = getActiveEnvironmentsForInstance(params.instanceId)
        .then(data => {
            return data || [];
        })
        .catch(error => {
            return [];
        });


    return defer({ environments: environmentsPromise });
}



function InstanceEnvironments(props) {
    const loaderDataPromise = useLoaderData()
    const {instanceId} = useParams()
    const [searchQuery, setSearchQuery] = useState('');
    function handleSearch(query) {
        setSearchQuery(query);
    }
    async function enableEnvironmentInInstance(envId) {
        const res = await toggleEnvironmentInInstance(instanceId, envId, true);
        if (res) {
            toast.success("Environment enabled in instance.");
        } else {
            toast.error("Operation failed.");
        }
    }
    async function disableEnvironmentInInstance(envId) {

        let imageUrls = [];

            const environments = await getToggleEnvironments(instanceId, id);
            for (const env of environments) {
                if (env.enabledValue && isImageUrl(env.enabledValue)) {
                    imageUrls.push(env.enabledValue);
                }
                if (env.disabledValue && isImageUrl(env.disabledValue)) {
                    imageUrls.push(env.disabledValue);
                }
            }

        for (const imageUrl of imageUrls) {
            await deleteFile(imageUrl);
        }

        const res = await toggleEnvironmentInInstance(instanceId, envId, false);
        if (res) {
            toast.success("Environment disabled in instance.");
        } else {
            toast.error("Operation failed.");
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

    function render(response){

        const environments = Array.isArray(response) ? response : [];

        const filteredEnvironments = environments?.filter((el) =>
            el.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        const sortedEnvironments = filteredEnvironments.sort((a, b) => {
            if(a.id < b.id) return -1;
            if(a.id > b.id) return 1;
            return 0;
        })

        const environmentsEl = sortedEnvironments.map(el => {
            return (
                <div className={"project-environment list-item"} key={el.id} style={{display:"flex", justifyContent:"space-between"}}>
                    <p>{el.name}</p>
                    <p style={{marginLeft:`${120-el.name.length*7.5}px`}}>{el.type}</p>
                    <div className={"list-item-actions"}>
                        <ProjectEnvironmentSwitch
                            checked={el.enabled}
                            handleEnable={() => enableEnvironmentInInstance(el.id)}
                            handleDisable={() => disableEnvironmentInInstance(el.id)}
                            resourceName={el.name}
                            enabledToggleCount={el.enabledInstanceToggleCount}
                        />
                    </div>
                </div>
                )
            })
        if(environments.length === 0){
            return (
                <EmptyList
                    resource={"environment"}
                    recommend={"Get started by enabling one."}
                />
            )
        } else {
            if(environmentsEl?.length === 0){
                return <EmptySearchResult
                    resource={"environment"}
                    searchValue={searchQuery}
                />
            } else {
                return environmentsEl
            }
        }
    }


    return (
        <>
            <ListPageHeader
                title={"Environments"}
                buttonText={"New environment"}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className={"list-container"}>
                <div className={"project-environment list-item item-header"} style={{display:"flex", justifyContent:"space-between"}}>
                    <p>Name</p>
                    <p>Type</p>
                    <p>Visible in instance</p>
                </div>
                <Suspense fallback={<LoadingBanner/>}>
                    <Await resolve={loaderDataPromise.environments}>
                        {
                            render
                        }
                    </Await>
                </Suspense>
            </div>
        </>
    )
}

export default InstanceEnvironments;
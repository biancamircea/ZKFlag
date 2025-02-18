import React, { useEffect, useState, Suspense } from "react";
import { Await, defer, useLoaderData, useNavigate, useParams } from "react-router-dom";
import { getAllInstancesFromProject, deleteInstance } from "../../api/instanceApi.js";
import { toast } from "react-toastify";
import ListPageHeader from "../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
import DeleteIcon from "../../components/ui/common/DeleteIcon.jsx";

export function loader({ params }) {
    return defer({ instances: getAllInstancesFromProject(params.projectId) });
}

function Instances() {
    const navigate = useNavigate();
    const loaderDataPromise = useLoaderData();
    const [allInstances, setAllInstances] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
    const { projectId } = useParams();

    async function deleteHandler(id) {
        const res = await deleteInstance(projectId, id);
        if (res) {
            toast.success("Instance deleted.");
            setAllInstances((prev) => prev.filter((el) => el.id !== id));
        } else {
            toast.error("Operation failed.");
        }
    }

    useEffect(() => {
        loaderDataPromise.instances.then((res) => setAllInstances(res));
    }, [loaderDataPromise]);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    function handleItemClick(id) {
        navigate(`/projects/${projectId}/instances/${id}`);
    }

    function renderInstances(response) {
        const filteredInstances = allInstances.filter((el) =>
            el.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        const sortedInstances = filteredInstances.sort((a, b) => {
            if (a.id < b.id) return -1;
            if (a.id > b.id) return 1;
            return 0;
        });

        const instancesEl = sortedInstances.map((el) => (
            <div className={"instances-list list-item item-body"} key={el.id} style={{display:"flex", justifyContent:"space-between"}}>
                <div
                    className={"instances-list-item-name"}
                    onClick={() => handleItemClick(el.id)}
                >
                    {el.name}
                </div>
                <div className={"context-fields list-item actions"} >
                    <DeleteIcon
                        resource={"Instance"}
                        resourceName={el.name}
                        deleteHandler={() => deleteHandler(el.id)}
                    />
                </div>
            </div>
        ));

        if (allInstances.length === 0) {
            return <EmptyList resource={"instances"} />;
        } else {
            if (instancesEl.length === 0) {
                return <EmptySearchResult resource={"instance"} searchValue={searchQuery} />;
            } else {
                return instancesEl;
            }
        }
    }

    return (
        <>
            <ListPageHeader
                title={"Instances"}
                buttonText={"New Instance"}
                hasButton={true}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
                onButtonClick={() => navigate(`/projects/${projectId}/instances/create`)}
            />
            <div className={"list-container"}>
                <div className={"context-fields list-item item-header"}>
                    <p>Name</p>
                    <p>Actions</p>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.instances}>
                        {renderInstances}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default Instances;

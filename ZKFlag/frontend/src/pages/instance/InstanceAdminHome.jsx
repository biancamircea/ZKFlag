import ListPageHeader from "../../components/ui/common/ListPageHeader.jsx";
import React, { Suspense, useEffect, useState } from "react";
import { Await, useLoaderData, defer } from "react-router-dom";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import { fetchUser, getInstancesForInstanceAdmin } from "../../api/userApi.js";
import { getProjectForInstance } from "../../api/instanceApi.js";
import { useNavigate } from "react-router-dom";

export async function loader() {
    const user = await fetchUser();
    if (!user || !user.id) {
        throw new Error("User ID is undefined.");
    }
    return defer({ instances: getInstancesForInstanceAdmin(user.id) });
}

function InstanceAdminHome() {
    const loaderDataPromise = useLoaderData();
    const [allInstances, setAllInstances] = useState([]);
    const [projects, setProjects] = useState({});
    const [searchQuery, setSearchQuery] = useState("");
    const [instanceCountEl, setInstanceCountEl] = useState(0);
    const navigate = useNavigate();

    function handleSearch(query) {
        setSearchQuery(query);
    }

    function handleItemClick(id) {
        const project = projects[id];
        if (project) {
            navigate(`/instances/${id}/projects/${project.id}`);
        }
    }

    useEffect(() => {
        loaderDataPromise.instances.then(async (data) => {
            setAllInstances(data);

            const projectData = {};
            for (const instance of data) {
                try {
                    const project = await getProjectForInstance(instance.id);
                    projectData[instance.id] = project;
                } catch (error) {
                    console.error(`Error fetching project for instance ${instance.id}:`, error);
                }
            }
            setProjects(projectData);
        }).catch(error => {
            console.error("Error fetching instances:", error);
        });
    }, [loaderDataPromise.instances]);

    function renderInstances(response) {
        if (allInstances.length === 0) {
            return <EmptyList resource={"instances"} />;
        }

        const filteredInstances = allInstances.filter(el =>
            el.name.toLowerCase().includes(searchQuery.toLowerCase())
        );
        setInstanceCountEl(filteredInstances.length);

        return (
            <div className="projects-list">
                {filteredInstances.length === 0 ? (
                    <EmptySearchResult resource={"instance"} searchValue={searchQuery} />
                ) : (
                    filteredInstances.map(el => (
                        <div className="project-list-item" key={el.id} onClick={() => handleItemClick(el.id)}>
                            <div className="project-list-item-top">
                                <p className="project-list-name">{el.name}</p>
                            </div>
                            <img src={"/images/project.png"} alt={"Project"} className={"project-list-logo"} />
                            <div className="project-list-details-container">
                                <div className="project-list-details">
                                    <p>Project</p>
                                    <span>{projects[el.id]?.name || "Loading..."}</span> {/* 🔹 Afișează numele proiectului */}
                                </div>
                                <div className="project-list-details">
                                    <p>Created At</p>
                                    <span>
                                        {el.startedAt ? new Date(el.startedAt).toLocaleDateString("ro") : "null"}
                                    </span>

                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        );
    }

    return (
        <>
            <ListPageHeader
                title={`Instances (${instanceCountEl})`}
                buttonText={""}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <Suspense fallback={<LoadingBanner />}>
                <Await resolve={loaderDataPromise.instances}>
                    {renderInstances}
                </Await>
            </Suspense>
        </>
    );
}

export default InstanceAdminHome;

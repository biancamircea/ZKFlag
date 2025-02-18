import ListPageHeader from "../../components/ui/common/ListPageHeader.jsx";
import React, {Suspense, useEffect, useState} from "react";
import {Await, defer, useLoaderData, useNavigate} from "react-router-dom";
import {deleteProject, getProjects} from "../../api/projectApi.js";
import EditIcon from "../../components/ui/common/EditIcon.jsx";
import DeleteIcon from "../../components/ui/common/DeleteIcon.jsx";
import {toast} from "react-toastify";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";

export function loader(){
    return defer({ projects: getProjects() })
}

function Projects() {
    const navigate = useNavigate();
    const [allProjects, setAllProjects]= useState([])
    const [searchQuery, setSearchQuery] = useState('');
    const loaderDataPromise = useLoaderData()
    const [projectCountEl, setProjectCountEl] = useState(0)
    let projectsElements = null
    function handleSearch(query) {
        setSearchQuery(query);
    }
    async function deleteHandler(id){
        const res = await deleteProject(id);
        if (res) {
            setAllProjects(prevProjects => prevProjects.filter(el => el.id !== id))
            toast.success("Project deleted.");
        } else {
            toast.error("Operation failed.");
        }
    }

    function handleItemClick(id){
        navigate(`/projects/${id}`)
    }

    useEffect(() => {
        async function fetchProjects() {
            const data = await loaderDataPromise.projects;
            setAllProjects(data.projects);
        }
        if (loaderDataPromise.projects) {
            fetchProjects();
        }
    }, [loaderDataPromise.projects]);

    function renderProjects(response){
        if(allProjects){
            const sortedProjects = allProjects.sort((a, b) => {
                if(a.id < b.id) return -1;
                if(a.id > b.id) return 1;
                return 0;
            })
            const filteredProjects = sortedProjects.filter((el) =>
                el.name.toLowerCase().includes(searchQuery.toLowerCase())
            );
            setProjectCountEl(filteredProjects.length)
            projectsElements = filteredProjects.map(el => (
                <div
                    className={"project-list-item"}
                    key={el.id}
                    onClick={() => handleItemClick(el.id)}
                >
                    <div className={"project-list-item-top"}>
                        <p className={"project-list-name"}>{el.name}</p>
                        <div className={"project-list-item-top-actions"}>
                            <EditIcon
                                id={el.id}
                            />
                            <DeleteIcon
                                resource={"Project"}
                                resourceName={el.name}
                                deleteHandler={() => deleteHandler(el.id)}
                            />
                        </div>

                    </div>
                    <img src={"/src/assets/images/project.png"} alt={"Project"} className={"project-list-logo"}/>
                    <div className={"project-list-details-container"}>
                        <div className={"project-list-details"}>
                            <p>Toggles</p>
                            <span>{el.toggleCount}</span>
                        </div>
                        <div className={"project-list-details"}>
                            <p>Members</p>
                            <span>{el.memberCount}</span>
                        </div>
                        <div className={"project-list-details"}>
                            <p>Tokens</p>
                            <span>{el.apiTokenCount}</span>
                        </div>
                    </div>
                </div>
            ))

        }
        if(allProjects.length === 0){
            return (
                <EmptyList
                    resource={"project"}
                />
            )
        } else {
            if(projectsElements?.length === 0){
                return <EmptySearchResult
                    resource={"project"}
                    searchValue={searchQuery}
                />
            } else {
                return projectsElements
            }
        }
    }

    return (
        <>
            <ListPageHeader
                title={`Projects (${projectCountEl})`}
                buttonText={"New project"}
                hasButton={true}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className={"projects-list"}>
                <Suspense fallback={<LoadingBanner/>}>
                    <Await resolve={loaderDataPromise.projects}>
                        {
                            renderProjects
                        }
                    </Await>
                </Suspense>
            </div>
        </>



    )
}

export default Projects
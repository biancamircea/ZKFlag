import React, { useEffect, useState, Suspense } from 'react';
import ListPageHeader from "../../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import { Await, defer, useLoaderData, useParams } from "react-router-dom";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import { getUserProjectsByUserId } from "../../../api/userApi.js";
import { removeAccessToProject } from "../../../api/projectApi.js";
import DeleteIcon from "../../../components/ui/common/DeleteIcon.jsx";
import { toast } from "react-toastify";
import { useRevalidator } from 'react-router-dom';

export async function loader({ params }) {
    return defer({ userProjects: getUserProjectsByUserId(params.userId) });
}

function UserProjectList() {
    const loaderDataPromise = useLoaderData();
    const [searchQuery, setSearchQuery] = useState('');
    const [userProjects, setUserProjects] = useState([]);
    const { userId } = useParams();
    const revalidator = useRevalidator();

    const refreshProjects = () => {
        revalidator.revalidate();
    }

    useEffect(() => {
        loaderDataPromise.userProjects.then(setUserProjects);
    }, []);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    async function handleRemove(projectId) {
        console.log("projectId", projectId)
        console.log("userId", userId)
        try {
            await removeAccessToProject(projectId, userId);
            toast.success("User removed from project!");
            refreshProjects()
            setUserProjects(prevProjects => prevProjects.filter(p => p.id !== projectId));
        } catch (error) {
            toast.error("Failed to remove user from project.");
        }
    }

   
    function renderProjects(projects) {
        if (!Array.isArray(projects)) {
            return <EmptyList resource="projects" />;
        }

        const filteredProjects = projects.filter(project =>
            project.projectName.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (filteredProjects.length === 0) {
            return <EmptyList resource="projects" />;
        }

        return filteredProjects.map(project => (
            <div className="context-fields list-item item-body" key={project.projectId}>
                <div className="tags-list-item">
                    <p>{project.projectName}</p>
                </div>
                <div className="list-item-actions" style={{marginLeft:"20px"}}>
                    <DeleteIcon deleteHandler={() => handleRemove(project.projectId) } />
                </div>
            </div>
        ));
    }

    return (
        <>
            <ListPageHeader
                title="User's Projects"
                buttonText={null}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className="list-container">
                <h2 className="admin-section-title">Projects</h2>
                <div className="context-fields list-item item-header">
                    <div className="tags-list-item">
                        <p>Name</p>
                    </div>
                    <div className="list-item-actions">
                        <p>Remove User from Project</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.userProjects}>
                        {renderProjects}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default UserProjectList;

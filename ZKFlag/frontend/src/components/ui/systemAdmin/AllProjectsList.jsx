import React, { useEffect, useState, Suspense } from 'react';
import ListPageHeader from "../../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import { Await, defer, useLoaderData, useNavigate } from "react-router-dom";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import { getProjects, deleteProject } from "../../../api/projectApi.js";
import DeleteIcon from "../../../components/ui/common/DeleteIcon.jsx";
import { toast } from "react-toastify";

export async function loader() {
    return defer({ projects: getProjects() });
}

function AllProjectsList() {
    const loaderDataPromise = useLoaderData();
    const [searchQuery, setSearchQuery] = useState('');
    const [projects, setProjects] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loaderDataPromise.projects.then(data => {
            if (data && Array.isArray(data.projects)) {
                setProjects(data.projects);
            } else {
                console.error("Expected an array but got:", data);
                setProjects([]);
            }
        });
    }, []);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    async function handleDelete(projectId) {
        try {
            const success = await deleteProject(projectId);
            if (success) {
                toast.success("Project deleted successfully!");
                setProjects(prevProjects => prevProjects.filter(p => p.id !== projectId));
            } else {
                toast.error("Failed to delete project.");
            }
        } catch (error) {
            toast.error("An error occurred while deleting the project.");
        }
    }

    function renderProjects(projects) {
        if (!projects.projects || projects.projects.length === 0) {
            return <EmptyList resource="projects" />;
        }

        const filteredProjects = projects.projects.filter(project =>
            project.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (filteredProjects.length === 0) {
            return <EmptyList resource="projects" />;
        }

        return filteredProjects.map(project => (
            <div
                className="context-fields list-item item-body"
                key={project.id}
                style={{ display: "flex", justifyContent: "space-between", alignItems: "center", cursor: "pointer" }}
            >
                <div
                    className="tags-list-item"
                    onClick={() => navigate(`/system-admin/projects/${project.id}/project-admins`)}
                    style={{ flexGrow: 1 }}
                >
                    <p>{project.name}</p>
                </div>
                <div className="list-item-actions">
                    <DeleteIcon deleteHandler={() => handleDelete(project.id)} />
                </div>
            </div>
        ));
    }

    return (
        <>
            <br/>
            <ListPageHeader
                title="All Projects"
                buttonText={"New project"}
                hasButton={true}
                searchQuery={searchQuery}
                handleSearch={handleSearch} // Trimiterea funcției de căutare
                auxiliaryPath="/system-admin/projects"
            />
            <div className="list-container">
                <h2 className="admin-section-title">Projects</h2>
                <div className="context-fields list-item item-header">
                    <div className="tags-list-item">
                        <p>Name</p>
                    </div>
                    <div className="list-item-actions">
                        <p>Actions</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.projects}>
                        {renderProjects}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default AllProjectsList;

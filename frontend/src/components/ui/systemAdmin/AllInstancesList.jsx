import React, { useEffect, useState, Suspense } from 'react';
import ListPageHeader from "../../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import { Await, useLoaderData, useNavigate } from "react-router-dom";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import { getProjects } from "../../../api/projectApi.js";
import { getAllInstancesFromProject, deleteInstance } from "../../../api/instanceApi.js";
import DeleteIcon from "../../../components/ui/common/DeleteIcon.jsx";
import { toast } from "react-toastify";

export async function loader() {
    const projects = await getProjects();
    const instancesByProject = await Promise.all(
        projects.projects.map(async (project) => {
            const instances = await getAllInstancesFromProject(project.id);
            return { projectId: project.id, instances };
        })
    );
    return { projects, instancesByProject };
}

function AllInstancesList() {
    const { projects, instancesByProject } = useLoaderData();
    const [searchQuery, setSearchQuery] = useState('');
    const navigate = useNavigate();

    function handleDeleteInstance(instanceId, projectId) {
        try {
           const res= deleteInstance(projectId, instanceId);
            toast.success("Instance deleted successfully!");
            const updatedInstances = instancesByProject.map(project => {
                if (project.projectId === projectId) {
                    return {
                        ...project,
                        instances: project.instances.filter(instance => instance.id !== instanceId),
                    };
                }
                return project;
            });
        } catch (error) {
            toast.error("Failed to delete instance.");
        }
    }

    function renderInstances(instances, projectId) {
        const filteredInstances = instances.filter(instance =>
            instance.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (filteredInstances.length === 0) {
            return <EmptyList resource="instance" />;
        }

        return filteredInstances.map(instance => (
            <div
                className="context-fields list-item item-body"
                key={instance.id}
                onClick={() => navigate(`/system-admin/${projectId}/instances/${instance.id}/instance-admins`)}
            >
                <div className="tags-list-item">
                    <p>{instance.name}</p>
                    <p>{instance.type}</p>
                </div>
                <div className="list-item-actions">
                    <DeleteIcon deleteHandler={() => handleDeleteInstance(instance.id, projectId)} />
                </div>
            </div>
        ));
    }

    function renderProjectsWithInstances() {
        if (!projects.projects || projects.projects.length === 0) {
            return <EmptyList resource="projects" />;
        }

        return projects.projects.map(project => {
            const projectInstances = instancesByProject.find(
                projectData => projectData.projectId === project.id
            )?.instances || [];

            return (
                <div key={project.id} className="project-instance-section">
                    <div className="project-title-container" style={{ display: "flex", justifyContent: "space-between" }}>
                        <h2 className="admin-section-title">{project.name}</h2>
                        <button
                            className="add-instance-button"
                            onClick={() => navigate(`/system-admin/instances/create/${project.id}`)}
                        >
                            Add Instance
                        </button>
                    </div>
                    <div className="context-fields list-item item-header">
                        <div className="tags-list-item">
                            <p>Name</p>
                        </div>
                        <div className="list-item-actions">
                            <p>Actions</p>
                        </div>
                    </div>
                    <Suspense fallback={<LoadingBanner />}>
                        {renderInstances(projectInstances, project.id)}
                    </Suspense>
                </div>
            );
        });
    }

    return (
        <>
            <br />
            <ListPageHeader
                title="All Instances by Project"
                buttonText={null}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={setSearchQuery}
            />
            <div className="list-container">
                {renderProjectsWithInstances()}
            </div>
        </>
    );
}

export default AllInstancesList;

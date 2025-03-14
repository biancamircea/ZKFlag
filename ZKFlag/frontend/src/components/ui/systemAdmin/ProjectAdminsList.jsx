import React, { useEffect, useState, Suspense } from 'react';
import ListPageHeader from "../../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import { Await, defer, useLoaderData, useParams } from "react-router-dom";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import { getUsersWithProjectAdminRole, getProjectById, removeAccessToProject, addAccessToProject } from "../../../api/projectApi.js";
import DeleteIcon from "../../../components/ui/common/DeleteIcon.jsx";
import { toast } from "react-toastify";

export async function loader({ params }) {
    return defer({
        projectAdmins: getUsersWithProjectAdminRole(params.projectId),
        project: getProjectById(params.projectId)
    });
}

function ProjectAdminsList() {
    const loaderDataPromise = useLoaderData();
    const [projectAdmins, setProjectAdmins] = useState([]);
    const [projectName, setProjectName] = useState("");
    const { projectId } = useParams();
    const [searchQuery, setSearchQuery] = useState('');

    useEffect(() => {
        loaderDataPromise.projectAdmins.then(setProjectAdmins);
        loaderDataPromise.project.then((project) => {
            setProjectName(project.name);
        });
    }, [loaderDataPromise]);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    async function handleRemoveAdmin(userId) {
        try {
            await removeAccessToProject(projectId, userId);
            toast.success("Admin removed from project.");
            setProjectAdmins(prevAdmins => prevAdmins.filter(admin => admin.id !== userId));
        } catch (error) {
            toast.error("Failed to remove admin from project.");
        }
    }

    async function handleAddAdmin() {
        const email = prompt("Enter the email of the user to add as Project Admin:");
        if (!email) return;

        try {
            await addAccessToProject(projectId, { email });
            toast.success("Admin added successfully.");
            loaderDataPromise.projectAdmins.then(setProjectAdmins);
        } catch (error) {
            toast.error("Failed to add admin to project.");
        }
    }

    function renderAdmins(admins) {
        if (!Array.isArray(admins)) {
            return <EmptyList resource="project admins" />;
        }

        const filteredAdmins = admins.filter(admin =>
            admin.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            admin.email.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (filteredAdmins.length === 0) {
            return <EmptyList resource="project admins" />;
        }

        return filteredAdmins.map(admin => (
            <div className="context-fields list-item item-body" key={admin.id}>
                <div className="tags-list-item">
                    <p>{admin.name}</p>
                    <p>{admin.email}</p>
                </div>
                <div className="list-item-actions">
                    <DeleteIcon deleteHandler={() => handleRemoveAdmin(admin.id)} />
                </div>
            </div>
        ));
    }

    return (
        <>
            <ListPageHeader
                title={`Project Admins for Project: ${projectName}`}
                buttonText={"Add Project Admin"}
                hasButton={true}
                handleSearch={handleSearch}
                searchQuery={searchQuery}
                auxiliaryPath={`/system-admin/projects/${projectId}`}
                onButtonClick={handleAddAdmin}
            />
            <div className="list-container">
                <h2 className="admin-section-title">Admins</h2>
                <div className="context-fields list-item item-header">
                    <div className="tags-list-item">
                        <p>Name</p>
                        <p>Email</p>
                    </div>
                    <div className="list-item-actions">
                        <p>Remove Admin from Project</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.projectAdmins}>
                        {renderAdmins}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default ProjectAdminsList;

import React, { useEffect, useState, Suspense } from 'react';
import ListPageHeader from "../../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import { Await, defer, useLoaderData, useParams } from "react-router-dom";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import { getUsersWithInstanceAdminRole, removeAccessToInstance, addAccessToInstance, getInstanceOverview } from "../../../api/instanceApi.js";
import { getAllUsersWithInstanceAdminRole } from "../../../api/userApi.js";
import DeleteIcon from "../../../components/ui/common/DeleteIcon.jsx";
import { toast } from "react-toastify";
import { getProjectById } from "../../../api/projectApi.js";

export async function loader({ params }) {
    return defer({
        instanceAdmins: getUsersWithInstanceAdminRole(params.instanceId),
        instance: getInstanceOverview(params.instanceId),
        allAdmins: getAllUsersWithInstanceAdminRole(),
        project: getProjectById(params.projectId)
    });
}

function InstanceAdminsList() {
    const loaderDataPromise = useLoaderData();
    const [instanceAdmins, setInstanceAdmins] = useState([]);
    const [instanceName, setInstanceName] = useState("");
    const [availableAdmins, setAvailableAdmins] = useState([]);
    const [selectedAdmin, setSelectedAdmin] = useState("");
    const { instanceId } = useParams();
    const [projectName, setProjectName] = useState("");
    const [searchQuery, setSearchQuery] = useState('');

    useEffect(() => {
        Promise.all([
            loaderDataPromise.instanceAdmins,
            loaderDataPromise.instance,
            loaderDataPromise.allAdmins,
            loaderDataPromise.project
        ]).then(([admins, instance, allAdmins, project]) => {
            setInstanceAdmins(admins);
            setInstanceName(instance.name);
            setAvailableAdmins(allAdmins.filter(admin => !admins.some(instAdmin => instAdmin.id === admin.id)));
            setProjectName(project.name);
        });
    }, [loaderDataPromise]);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    async function handleRemoveAdmin(userId) {
        try {
            await removeAccessToInstance(instanceId, userId);
            toast.success("Admin removed from instance.");
            setInstanceAdmins(prevAdmins => prevAdmins.filter(admin => admin.id !== userId));
        } catch (error) {
            toast.error("Failed to remove admin from instance.");
        }
    }

    async function handleAddAdmin() {
        if (!selectedAdmin) {
            toast.error("Please select a user!");
            return;
        }

        try {
            const requestData = {
                users: [{ id: parseInt(selectedAdmin) }]
            };

            await addAccessToInstance(instanceId, requestData);
            toast.success("Admin added successfully!");
            setInstanceAdmins(prevAdmins => [...prevAdmins, availableAdmins.find(admin => admin.id === parseInt(selectedAdmin))]);
            setAvailableAdmins(prevAdmins => prevAdmins.filter(admin => admin.id !== parseInt(selectedAdmin)));
            setSelectedAdmin("");
        } catch (error) {
            toast.error("Failed to add admin to instance.");
        }
    }

    function renderAdmins(admins) {
        if (!admins || admins.length === 0) {
            return <EmptyList resource="instance admins" />;
        }

        const filteredAdmins = admins.filter((admin) =>
            admin.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            admin.email.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (filteredAdmins.length === 0) {
            return <EmptySearchResult resource="admin" searchValue={searchQuery} />;
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
                title={`Instance Admins for Instance: ${projectName}/${instanceName}`}
                buttonText={"Add Instance Admin"}
                hasButton={true}
                handleSearch={handleSearch}
                auxiliaryPath={`/system-admin/instances/${instanceId}`}
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
                        <p>Remove Admin from Instance</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.instanceAdmins}>
                        {renderAdmins}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default InstanceAdminsList;

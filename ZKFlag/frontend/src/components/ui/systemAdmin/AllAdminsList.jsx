import React, { useEffect, useState, Suspense } from "react";
import ListPageHeader from "../../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import { Await, defer, useLoaderData, useNavigate } from "react-router-dom";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../../../components/ui/common/EmptySearchResult.jsx";
import { getAllUsersWithInstanceAdminRole, getAllUsersWithProjectAdminRole, getUsersWithSystemAdminRole, deleteUser } from "../../../api/userApi.js";
import DeleteIcon from "../../../components/ui/common/DeleteIcon.jsx";
import { toast } from "react-toastify";
import { useAuth } from "../../../AuthContext.jsx"
import { useRevalidator } from 'react-router-dom';

export async function loader() {
    return defer({
        instanceAdmins: getAllUsersWithInstanceAdminRole(),
        projectAdmins: getAllUsersWithProjectAdminRole(),
        systemAdmins: getUsersWithSystemAdminRole(),
    });
}

function AllAdminsList() {
    const loaderDataPromise = useLoaderData();
    const [searchQuery, setSearchQuery] = useState('');
    const [instanceAdmins, setInstanceAdmins] = useState([]);
    const [projectAdmins, setProjectAdmins] = useState([]);
    const [systemAdmins, setSystemAdmins] = useState([]);
    const revalidator = useRevalidator();

    const refreshAdmins = () => {
        revalidator.revalidate();
    };

    const { user, logout } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        loaderDataPromise.instanceAdmins.then(setInstanceAdmins);
        loaderDataPromise.projectAdmins.then(setProjectAdmins);
        loaderDataPromise.systemAdmins.then(setSystemAdmins);
    }, []);

    function handleSearch(query) {
        setSearchQuery(query);
    }

    async function handleDelete(userId, setAdmins) {
        try {
            await deleteUser(userId);
            toast.success("User deleted successfully!");
            refreshAdmins();
            setAdmins(prevAdmins => prevAdmins.filter(admin => admin.id !== userId));

            if (user && user.id === userId) {
                toast.info("You deleted your own account. Logging out...");
                logout();
                navigate("/login");
            }
        } catch (error) {
            toast.error("Failed to delete user.");
        }
    }

    function renderAdmins(admins, setAdmins,type) {
        const filteredAdmins = admins.filter((admin) =>
            admin.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            admin.email.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (admins.length === 0) {
            return <EmptyList resource="admins" />;
        }

        if (filteredAdmins.length === 0) {
            return <EmptySearchResult resource="admin" searchValue={searchQuery} />;
        }

        return filteredAdmins.map(admin => (
            <div
                className="context-fields list-item item-body"
                key={admin.id}
                style={{ cursor: type === "project" || type === "instance" ? "pointer" : "default" }}
            >
                <div
                    className="tags-list-item"
                    onClick={() => {
                        if (type === "project") {
                            navigate(`/system-admin/${admin.id}/projects`);
                        }
                        if (type === "instance") {
                            navigate(`/system-admin/${admin.id}/instances`);
                        }
                    }}
                    style={{contentAlign:"center", textAlign:"center"}}
                >
                    <p>{admin.name}</p>
                    <p>{admin.email}</p>
                </div>
                <div className="list-item-actions" style={{marginLeft:"20px"}}>
                    <DeleteIcon deleteHandler={() => handleDelete(admin.id, setAdmins)} />
                </div>
            </div>

        ));
    }

    return (
        <>
            <br/>
            <ListPageHeader
                title="Admins"
                buttonText={null}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className="list-container">
                <h2 className="admin-section-title">Instance Admins</h2>
                <div className="context-fields list-item item-header">
                    <div className="tags-list-item">
                        <p>Name</p>
                        <p>Email</p>
                    </div>
                    <div className="list-item-actions">
                        <p>Actions</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.instanceAdmins}>
                        {(admins) => renderAdmins(admins, setInstanceAdmins,"instance")}
                    </Await>
                </Suspense>

                <h2 className="admin-section-title">Project Admins</h2>
                <div className="context-fields list-item item-header">
                    <div className="tags-list-item">
                        <p>Name</p>
                        <p>Email</p>
                    </div>
                    <div className="list-item-actions">
                        <p>Actions</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.projectAdmins}>
                        {(admins) => renderAdmins(admins, setProjectAdmins,"project")}
                    </Await>
                </Suspense>

                <h2 className="admin-section-title">System Admins</h2>
                <div className="context-fields list-item item-header">
                    <div className="tags-list-item">
                        <p>Name</p>
                        <p>Email</p>
                    </div>
                    <div className="list-item-actions">
                        <p>Actions</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.systemAdmins}>
                        {(admins) => renderAdmins(admins, setSystemAdmins,"system")}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default AllAdminsList;

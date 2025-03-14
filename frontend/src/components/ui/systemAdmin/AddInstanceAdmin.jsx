import React, { Suspense, useEffect, useState } from 'react';
import { Await, defer, Form, useLoaderData, useNavigate, useParams } from "react-router-dom";
import {  getUsersWithInstanceAdminRole, addAccessToInstance } from "../../../api/instanceApi.js";
import { getAllUsersWithInstanceAdminRole } from "../../../api/userApi.js";
import { toast } from "react-toastify";
import CancelButton from "../../../components/ui/common/CancelButton.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";

export async function loader({ params }) {
    return defer({
        allAdmins: getAllUsersWithInstanceAdminRole(),
        alreadyAdmins: getUsersWithInstanceAdminRole(params.instanceId),
    });
}

function AddInstanceAdmin() {
    const loaderDataPromise = useLoaderData();
    const [availableAdmins, setAvailableAdmins] = useState([]);
    const [selectedAdmin, setSelectedAdmin] = useState("");
    const [searchQuery, setSearchQuery] = useState(""); // 🔹 Adăugăm Search Query
    const navigate = useNavigate();
    const { instanceId } = useParams();

    useEffect(() => {
        Promise.all([loaderDataPromise.allAdmins, loaderDataPromise.alreadyAdmins])
            .then(([allAdmins, alreadyAdmins]) => {
                const filteredAdmins = allAdmins.filter(admin =>
                    !alreadyAdmins.some(existingAdmin => existingAdmin.id === admin.id)
                );
                setAvailableAdmins(filteredAdmins);
            });
    }, [loaderDataPromise]);

    async function handleSubmit(event) {
        event.preventDefault();

        if (!selectedAdmin) {
            toast.error("Please select a user!");
            return;
        }

        try {
            const requestData = {
                users: [{ id: selectedAdmin }]
            };

            await addAccessToInstance(instanceId, requestData);
            toast.success("Admin added successfully!");
            navigate(-1);
        } catch (error) {
            toast.error("Failed to add admin.");
        }
    }

    const filteredAdmins = availableAdmins.filter(admin =>
        admin.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        admin.email.toLowerCase().includes(searchQuery.toLowerCase())
    );

    function renderForm(admins) {
        return (
            <div>
                <br />
                <Form className={"create-form-container"} onSubmit={handleSubmit} method={"post"}>
                    <div className={"create-form-fields"}>
                        <span className={"title"}>Add Instance Admin</span>

                        <div className={"create-form-field-item"}>
                            <label htmlFor={"admin"}>Choose a user:</label>
                            <select
                                id={"admin"}
                                name={"admin"}
                                value={selectedAdmin}
                                onChange={(e) => setSelectedAdmin(e.target.value)}
                            >
                                <option value="">-- Select user --</option>
                                {filteredAdmins.map((admin) => (
                                    <option key={admin.id} value={admin.id}>
                                        {admin.name} ({admin.email})
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className={"create-form-buttons"}>
                        <CancelButton />
                        <button type={"submit"}>Add Admin</button>
                    </div>
                </Form>
            </div>
        );
    }

    return (
        <Suspense fallback={<LoadingBanner />}>
            <Await resolve={availableAdmins}>
                {renderForm}
            </Await>
        </Suspense>
    );
}

export default AddInstanceAdmin;

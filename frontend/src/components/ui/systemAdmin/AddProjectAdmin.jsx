import React, { Suspense, useEffect, useState } from 'react';
import { Await, defer, Form, useLoaderData, useNavigate, useParams } from "react-router-dom";
import { getAllUsersWithProjectAdminRole } from "../../../api/userApi.js";
import { addAccessToProject, getUsersWithProjectAdminRole } from "../../../api/projectApi.js";
import { toast } from "react-toastify";
import CancelButton from "../../../components/ui/common/CancelButton.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";

export async function loader({ params }) {
    return defer({
        allAdmins: getAllUsersWithProjectAdminRole(),
        alreadyAdmins: getUsersWithProjectAdminRole(params.projectId),
    });
}

function AddProjectAdmin() {
    const loaderDataPromise = useLoaderData();
    const [availableAdmins, setAvailableAdmins] = useState([]);
    const [selectedAdmin, setSelectedAdmin] = useState("");
    const [searchQuery, setSearchQuery] = useState(""); // 🔹 Adăugat searchQuery
    const navigate = useNavigate();
    const { projectId } = useParams();

    useEffect(() => {
        Promise.all([loaderDataPromise.allAdmins, loaderDataPromise.alreadyAdmins])
            .then(([allAdmins, alreadyAdmins]) => {
                const filteredAdmins = allAdmins.filter(admin =>
                    !alreadyAdmins.some(alreadyAdmin => alreadyAdmin.id === admin.id)
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

            await addAccessToProject(projectId, requestData);
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
                        <span className={"title"}>Add Project Admin</span>
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

                    {/* Butoane de acțiune */}
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

export default AddProjectAdmin;

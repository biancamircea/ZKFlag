import React, { useEffect, useState, Suspense } from 'react';
import ListPageHeader from "../../../components/ui/common/ListPageHeader.jsx";
import LoadingBanner from "../../../components/ui/common/LoadingBanner.jsx";
import { Await, defer, useLoaderData, useParams } from "react-router-dom";
import EmptyList from "../../../components/ui/common/EmptyList.jsx";
import { getUserInstancesByUserId } from "../../../api/userApi.js";
import { removeAccessToInstance } from "../../../api/instanceApi.js";
import DeleteIcon from "../../../components/ui/common/DeleteIcon.jsx";
import { toast } from "react-toastify";
import { useRevalidator } from 'react-router-dom';

export async function loader({ params }) {
    return defer({ userInstances: getUserInstancesByUserId(params.userId) });
}

function UserInstanceList() {
    const loaderDataPromise = useLoaderData();
    const [searchQuery, setSearchQuery] = useState('');
    const [userInstances, setUserInstances] = useState([]);
    const { userId } = useParams();
    const revalidator = useRevalidator();

    const refreshProjects = () => {
        revalidator.revalidate();
    }

    useEffect(() => {
        loaderDataPromise.userInstances.then(setUserInstances);
    }, []);


    function handleSearch(query) {
        setSearchQuery(query);
    }

    async function handleRemove(instanceId) {
        try {
            await removeAccessToInstance(instanceId, userId);
            toast.success("User removed from instance!");
            refreshProjects()
            setUserInstances(prevInstances => prevInstances.filter(i => i.id !== instanceId));
        } catch (error) {
            toast.error("Failed to remove user from instance.");
        }
    }

    function renderInstances(instances) {
        if (!Array.isArray(instances)) {
            return <EmptyList resource="instances" />;
        }

        const filteredInstances = instances.filter(instance =>
            instance.instanceName.toLowerCase().includes(searchQuery.toLowerCase())
        );

        if (filteredInstances.length === 0) {
            return <EmptyList resource="instances" />;
        }

        return filteredInstances.map(instance => (
            <div className="context-fields list-item item-body" key={instance.id}>
                <div className="tags-list-item">
                    <p>{instance.instanceName}</p>
                </div>
                <div className="list-item-actions" style={{marginLeft:"20px"}}>
                    <DeleteIcon deleteHandler={() => handleRemove(instance.id)} />
                </div>
            </div>
        ));
    }

    return (
        <>
            <ListPageHeader
                title="User's Instances"
                buttonText={null}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className="list-container">
                <h2 className="admin-section-title">Instances</h2>
                <div className="context-fields list-item item-header">
                    <div className="tags-list-item">
                        <p>Name</p>
                    </div>
                    <div className="list-item-actions">
                        <p>Remove User from Instance</p>
                    </div>
                </div>
                <Suspense fallback={<LoadingBanner />}>
                    <Await resolve={loaderDataPromise.userInstances}>
                        {renderInstances}
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default UserInstanceList;

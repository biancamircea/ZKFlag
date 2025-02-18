import ListPageHeader from "../../components/ui/common/ListPageHeader.jsx";
import CustomSwitch from "../../components/ui/common/CustomSwitch.jsx";
import {Await, defer, useLoaderData, useNavigate} from "react-router-dom";
import React, {Suspense, useState, useEffect} from "react";
import {deleteEnvironment, getEnvironments, toggleEnvironment} from "../../api/environmentApi.js";
import {toast} from "react-toastify";
import DeleteIcon from "../../components/ui/common/DeleteIcon.jsx";
import EditIcon from "../../components/ui/common/EditIcon.jsx";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";

export function loader(){
    return defer({ env: getEnvironments() })
}

function Environments() {
    const navigate = useNavigate();
    const envDataPromise = useLoaderData()
    const [allEnvironments, setAllEnvironments]= useState([])
    const [searchQuery, setSearchQuery] = useState('');

    function handleSearch(query) {
        setSearchQuery(query);
    }

    useEffect(() => {
        async function fetchEnvironments() {
            const data = await envDataPromise.env;
            setAllEnvironments(data.environments);
        }
        if (envDataPromise.env) {
            fetchEnvironments();
        }
    }, [envDataPromise.env]);

    async function deleteEnv(id){
        const res = await deleteEnvironment(id);
        if (res.status === 204) {
            setAllEnvironments(prevEnvironments => prevEnvironments.filter(el => el.id !== id))
            toast.success("Environment deleted.");
        } else {
            toast.error("Operation failed");
        }
    }

    async function enableEnvironment(id) {
        const res = await toggleEnvironment(id, true);
        if (res) {

            toast.success("Environment enabled.");
        } else {
            toast.error("Operation failed.");
        }
    }

    function clearProjectCount(id){
        return allEnvironments.map(env => {
            if (env.id === id) {
                // update the projectCount of the environment with the given id
                return { ...env, projectCount: 0 };
            } else {
                return env;
            }
        });
    }

    async function disableEnvironment(id) {
        const res = await toggleEnvironment(id, false);
        if (res) {
            const updatedEnvironments = clearProjectCount(id)
            setAllEnvironments(updatedEnvironments);
            toast.success("Environment disabled.");
        } else {
            toast.error("Operation failed.");
        }
    }

    function handleItemClick(id){
        navigate(`edit/${id}`)
    }

    function renderEnvironments(response) {
        let envElements = null
        if(allEnvironments){
            const sortedEnvironments = allEnvironments.sort((a, b) => {
                if(a.id < b.id) return -1;
                if(a.id > b.id) return 1;
                return 0;
            })
            const filteredEnvironments = sortedEnvironments.filter((el) =>
                el.name.toLowerCase().includes(searchQuery.toLowerCase())
            );
            envElements = filteredEnvironments.map(el => (
                <div
                    className={"list-item item-body"}
                    key={el.id}
                    onClick={() => handleItemClick(el.id)}
                >
                    <p id={"env-name"}>{el.name}</p>
                    <p>{el.type}</p>
                    <p>{el.projectCount}</p>
                    <p>{el.apiTokenCount}</p>
                    <div className={"list-item-actions"}>
                        <CustomSwitch
                            checked={el.enabled}
                            handleEnable={() => enableEnvironment(el.id)}
                            handleDisable={() => disableEnvironment(el.id)}
                            resourceName={el.name}
                            enabledToggleCount={el.enabledToggleCount}
                        />
                        <EditIcon
                            id={el.id}
                        />
                        <DeleteIcon
                            resource={"Environment"}
                            resourceName={el.name}
                            deleteHandler={() => deleteEnv(el.id)}
                        />
                    </div>
                </div>
            ))
        }
        if(allEnvironments.length === 0){
            return (
                <EmptyList
                    resource={"environment"}
                />
            )
        } else {
            if(envElements?.length === 0){
                return <EmptySearchResult
                    resource={"environment"}
                    searchValue={searchQuery}
                />
            } else {
                return envElements
            }
        }
    }


    return (
        <>
            <ListPageHeader
                title={"Environments"}
                buttonText={"New environment"}
                hasButton={true}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            <div className={"list-container"}>
                <div className={"list-item item-header"}>
                    <p>Name</p>
                    <p>Type</p>
                    <p></p>
                    <p>API Tokens</p>
                    <p>Actions</p>
                </div>
                <Suspense fallback={<LoadingBanner/>}>
                    <Await resolve={envDataPromise.env}>
                        {
                            renderEnvironments
                        }
                    </Await>
                </Suspense>
            </div>
        </>
    );
}

export default Environments;


import ListPageHeader from "../components/ui/common/ListPageHeader.jsx";
import React, {Suspense, useEffect, useState} from "react";
import LoadingBanner from "../components/ui/common/LoadingBanner.jsx";
import {Await, defer, useLoaderData, useNavigate, useParams} from "react-router-dom";
import {deleteContextField, getContextFields} from "../api/contextFieldApi.js";
import EditIcon from "../components/ui/common/EditIcon.jsx";
import DeleteIcon from "../components/ui/common/DeleteIcon.jsx";
import EmptySearchResult from "../components/ui/common/EmptySearchResult.jsx";
import EmptyList from "../components/ui/common/EmptyList.jsx";
import {toast} from "react-toastify";
import { Info } from "react-feather";
import Tooltip from "@mui/material/Tooltip";

export async function loader({ params }){
    return defer({ contextFields: getContextFields(params.projectId) })
}


function ContextFields() {
    const navigate = useNavigate();
    const loaderDataPromise = useLoaderData()
    const [allContextFields, setAllContextFields] = useState([])
    const [searchQuery, setSearchQuery] = useState('');
    const { projectId } = useParams();
    async function deleteHandler(id){
        const res = await deleteContextField(projectId, id);
        if (res) {
            toast.success("Context field deleted.");
            setAllContextFields(prevElements => prevElements.filter(el => el.id !== id));
        } else {
            toast.error("Operation failed.");
        }
    }

    useEffect(() => {
        loaderDataPromise.contextFields.then((res) =>  setAllContextFields(res['context-fields']))
    }, [])

    function handleSearch(query) {
        setSearchQuery(query);
    }

    function handleItemClick(id){
        navigate(`edit/${id}`)
    }

    function render(response){
        const filteredContextFields = allContextFields?.filter((el) =>
            el.name.toLowerCase().includes(searchQuery.toLowerCase())
        );

        const sortedContextFields = filteredContextFields?.sort((a, b) => {
            if(a.id < b.id) return -1;
            if(a.id > b.id) return 1;
            return 0;
        })

        const contextFieldsEl = sortedContextFields.map(el => (
            <div
                className={"context-fields list-item item-body"}
                key={el.id}
                onClick={() => handleItemClick(el.id)}
            >
                <div className={"name-and-description"}>
                    <p>{el.name}</p>
                    <span className={"gray-text"}>{el.description === "" ? "No description" : el.description}</span>
                </div>
                <div style={{ display: "flex", justifyContent: "center" }}>
                    <p>
                        {el.isConfidential === 2
                            ? "Location evaluation"
                            : el.isConfidential === 1
                                ? "ZKP evaluation"
                                : "Normal evaluation"}
                    </p>
                </div>

                <div className={"context-fields list-item actions"}>
                    <EditIcon
                        id={el.id}
                    />
                    <DeleteIcon
                        resource={"Context field"}
                        resourceName={el.name}
                        deleteHandler={() => deleteHandler(el.id)}
                    />
                </div>
            </div>
        ))
        if(allContextFields.length === 0){
            return (
                <EmptyList
                    resource={"context field"}
                />
            )
        } else {
            if(contextFieldsEl?.length === 0){
                return <EmptySearchResult
                    resource={"context fields"}
                    searchValue={searchQuery}
                />
            } else {
                return contextFieldsEl
            }
        }
    }

    return (
        <>
            <ListPageHeader
                title={"Context fields"}
                buttonText={"New context field"}
                hasButton={true}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
                hasInfo={true}
                infoText={"Context fields are used to create constraints. They can be used to manage feature toggles and other configurations."}
            />
            <div className={"list-container"}>
                <div className="context-fields list-item item-header" style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    width: "100%"
                }}>
                    <p style={{ flex: 1, display:"flex", justifyContent:"flex-start" }}>Name</p>
                    <div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent:"center",gap: "4px"}}>
                        <p>Type</p>
                        <Tooltip title={"ZKP context field values won't be transmitted to the server - only a cryptographic proof will be sent."} arrow>
                            <span>
                                <Info className="info-icon" size={18} />
                            </span>
                        </Tooltip>
                    </div>
                    <p style={{ flex: 1, display:"flex", justifyContent:"flex-end" }}>Actions</p>
                </div>
                <Suspense fallback={<LoadingBanner/>}>
                    <Await resolve={loaderDataPromise.contextFields}>
                        {
                            render
                        }
                    </Await>
                </Suspense>
            </div>
        </>
    )
}

export default ContextFields
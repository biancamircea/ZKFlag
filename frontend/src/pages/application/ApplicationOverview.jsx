import React, {useState} from 'react';
import {useOutletContext} from "react-router-dom";
import ApplicationInstancesList from "../../components/ui/application/ApplicationInstancesList.jsx";
import ListPageHeader from "../../components/ui/common/ListPageHeader.jsx";
import EmptyList from "../../components/ui/common/EmptyList.jsx";
import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
import DeleteBtnDialog from "../../components/ui/common/DeleteBtnDialog.jsx";
import {deleteApplicationInstances} from "../../api/applicationApi.js";
import {toast} from "react-toastify";

function ApplicationOverview(props) {
    const { application, deleteInstanceHandler, deleteAllInstancesHandler } = useOutletContext()
    const [searchQuery, setSearchQuery] = useState('');

    function handleSearch(query) {
        setSearchQuery(query);
    }

    const filteredInstances = application.instances.filter((el) =>
        el.nameId.toLowerCase().includes(searchQuery.toLowerCase())
    );


    function deleteInstances(){
        const response = deleteApplicationInstances(application.id)
        if(response){
            toast.success("Instances deleted.")
            deleteAllInstancesHandler()
        } else {
            toast.error("Operation failed.")
        }
    }

    function renderList(){
        if(application.instances.length === 0){
            return (<EmptyList resource={"instance"} recommend={"Get started by using a toggle in a project."}/>)
        } else {
            if(filteredInstances.length === 0){
                return (<EmptySearchResult resource={"instance"} searchValue={searchQuery}/>)
            } else {
                return (
                    <ApplicationInstancesList
                        instances={filteredInstances}
                        deleteHandler={deleteInstanceHandler}
                    />
                )
            }
        }
    }

    return (
        <>
            <div className={"flex-between-container"}>
                <h4>{application.instances.length} instances registered</h4>
                {
                    application.instances.length > 0 &&
                    (
                        <DeleteBtnDialog
                            btnLabel={"Delete instances"}
                            deleteHandler={deleteInstances}
                            resource={"ALL Instances!"}
                            resourceName={"the entire list of instances for this application"}
                        />
                    )
                }
            </div>
            <ListPageHeader
                title={`Instances`}
                buttonText={""}
                hasButton={false}
                searchQuery={searchQuery}
                handleSearch={handleSearch}
            />
            {
                renderList()
            }

        </>
    );
}

export default ApplicationOverview;
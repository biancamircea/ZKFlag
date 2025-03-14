import React from 'react';

function EventsListItem({event, search}) {
    const {id, project,instance, environment, toggle, action, createdAt} = event
    let result = ""
    function renderText(){

        if(project && toggle && environment && instance){
            switch (action){
                case "ENABLE":
                    result = `Toggle '${toggle}' from project '${project}', instance '${instance}' ENABLED in environment '${environment}'.`
                    break
                case "DISABLE":
                    result = `Toggle '${toggle}' from project '${project}', instance '${instance}' DISABLED in environment '${environment}'.`
                    break
                case "UPDATE":
                    result = `Configuration of toggle '${toggle}' from project '${project}' UPDATED'.`
                    break
                default:
                    result = "NOne"
            }
        }else if(project && toggle && !instance) {
            switch (action){
                case "CREATE":
                    result = `Toggle '${toggle}' CREATED in project '${project}'.`
                    break
                case "UPDATE":
                    result = `Toggle '${toggle}' UPDATED in project '${project}'.`
                    break
                default:
                    result = "NOne"
            }
        } else if(instance && !toggle && environment) {
            switch (action){
                case "ENABLE":
                    result = `Environment '${environment}' ENABLED in instance ${instance}.`
                    break
                case "DISABLE":
                    result = `Environment '${environment}' DISABLED in instance ${instance}.`
                    break
                default:
                    result = "NOne"
            }
        } else if(project && !toggle && !instance){
            switch (action){
                case "CREATE":
                    result = `Project '${project}' CREATED.`
                    break
                case "UPDATE":
                    result = `Project '${project}' UPDATED.`
                    break
                case "DELETE":
                    result = `Project '${project}' DELETED.`
                    break
                default:
                    result = "NOne"
            }
        } else if(!project && !toggle && environment && instance){
            switch (action){
                case "ENABLE":
                    result = `Environment '${environment}' ENABLED in instance '${instance}'.`
                    break
                case "DISABLE":
                    result = `Environment '${environment}' DISABLED in instance '${instance}'.`
                    break
                case "CREATE":
                    result = `Environment '${environment}' CREATED.`
                    break
                case "UPDATE":
                    result = `Environment '${environment}' UPDATED.`
                    break
                case "DELETE":
                    result = `Environment '${environment}' DELETED.`
                    break
                default:
                    result = "NOne"
            }
        }else if(project && !toggle && instance && !environment){
            switch (action){
                case "CREATE":
                    result = `Instance '${instance}' CREATED in project ${project}.`
                    break
                case "UPDATE":
                    result = `Instance '${instance}' UPDATED in project ${project}.`
                    break
                case "DELETE":
                    result = `Instance '${instance}' DELETED from project ${project}.`
                    break
                default:
                    result = "NOne"
            }
        }else if(!project && !toggle && !instance && environment) {
            switch (action) {
                case "CREATE":
                    result = `Environment '${environment}' CREATED.`
                    break
                case "UPDATE":
                    result = `Environment '${environment}' UPDATED.`
                    break
                case "DELETE":
                    result = `Environment '${environment}' DELETED.`
                    break
                case "ENABLE":
                    result = `Environment '${environment}' ENABLED in platform.`
                    break
                case "DISABLE":
                    result = `Environment '${environment}' DISABLED in platform.`
                    break
                default:
                    result = "NOne"
            }
        }

        return result
    }

    renderText()

    function render(){
        if(search){
            if(result.toLowerCase().includes(search.toLowerCase())) {
                return (
                    <div className={"list-item item-body events"}>
                        <p className={"underlined-on-parent-hover"}>
                            {`#${id} `}
                            {
                                result
                            }
                        </p>
                        <span className={"gray-text"}>{
                            createdAt ?
                                new Date(createdAt).toLocaleString("ro") :
                                "null"
                        }</span>
                    </div>
                )
            } else {
                return null
            }
        } else {
            return (
                <div className={"list-item item-body events"}>
                    <p className={"underlined-on-parent-hover"}>
                        {`#${id} `}
                        {
                            renderText()
                        }
                    </p>
                    <span className={"gray-text"}>{
                        createdAt ?
                            new Date(createdAt).toLocaleString("ro") :
                            "null"
                    }</span>
                </div>
            )
        }
    }

    return (
        <>
            {render()}
        </>
    );
}

export default EventsListItem;
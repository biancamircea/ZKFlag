import React from 'react';

function EventsListItem({event, search}) {
    const {id, project, environment, toggle, action, createdAt} = event
    let result = ""
    function renderText(){

        if(project && toggle && environment){
            switch (action){
                case "ENABLE":
                    result = `Instance of toggle '${toggle}' from project '${project}' ENABLED in environment '${environment}'.`
                    break
                case "DISABLE":
                    result = `Instance of toggle '${toggle}' from project '${project}' DISABLED in environment '${environment}'.`
                    break
                case "UPDATE":
                    result = `Configuration of toggle '${toggle}' from project '${project}' UPDATED in environment '${environment}'.`
                    break
                default:
                    result = "NOne"
            }
        } else if(project && toggle && !environment) {
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
        } else if(project && !toggle && environment) {
            switch (action){
                case "ENABLE":
                    result = `Environment '${environment}' ENABLED in project ${project}.`
                    break
                case "DISABLE":
                    result = `Environment '${environment}' DISABLED in project ${project}.`
                    break
                default:
                    result = "NOne"
            }
        } else if(project && !toggle && !environment){
            switch (action){
                case "CREATE":
                    result = `Project '${project}' CREATED.`
                    break
                case "UPDATE":
                    result = `Project '${project}' UPDATED.`
                    break
                default:
                    result = "NOne"
            }
        } else if(!project && !toggle && environment){
            switch (action){
                case "ENABLE":
                    result = `Environment '${environment}' ENABLED in app.`
                    break
                case "DISABLE":
                    result = `Environment '${environment}' DISABLED in app.`
                    break
                case "CREATE":
                    result = `Environment '${environment}' CREATED.`
                    break
                case "UPDATE":
                    result = `Environment '${environment}' UPDATED.`
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
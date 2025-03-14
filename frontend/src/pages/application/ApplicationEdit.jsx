// import React from 'react';
// import UpdateApplicationForm from "../../components/forms/UpdateApplicationForm.jsx";
// import {useNavigate, useOutletContext, useParams} from "react-router-dom";
// import {toast} from "react-toastify";
// import {updateApplication} from "../../api/applicationApi.js";
//
// function ApplicationEdit(props) {
//     const { application, updateHandler } = useOutletContext()
//     const navigate = useNavigate()
//     const { appId } = useParams();
//     async function handleSubmit(event){
//         event.preventDefault();
//         const formData = new FormData(event.target);
//         const description = formData.get("description");
//         const url = formData.get("appUrl");
//         if(application.description === description && application.url === url){
//             // nothing changed
//             toast.success("Nothing changed.")
//             navigate(-1)
//         } else {
//             // PUT request
//             const requestSuccessful = await updateApplication(appId,{ description, url })
//             updateHandler(requestSuccessful)
//             toast.success("Application updated.")
//             navigate(-1)
//         }
//     }
//
//     return (
//         <UpdateApplicationForm
//             appUrl={application.url}
//             description={application.description}
//             handleSubmit={handleSubmit}
//         />
//     );
// }
//
// export default ApplicationEdit;
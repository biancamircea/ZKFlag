// import ListPageHeader from "../../components/ui/common/ListPageHeader.jsx";
// import React, {Suspense, useState} from "react";
// import {Await, defer, useLoaderData} from "react-router-dom";
// import {getApplications} from "../../api/applicationApi.js";
// import LoadingBanner from "../../components/ui/common/LoadingBanner.jsx";
// import EmptyList from "../../components/ui/common/EmptyList.jsx";
// import EmptySearchResult from "../../components/ui/common/EmptySearchResult.jsx";
// import ApplicationsList from "../../components/ui/application/ApplicationsList.jsx";
//
// export function loader(){
//     return defer({ applications: getApplications() })
// }
//
// function Applications() {
//     const loaderDataPromise = useLoaderData()
//     const [searchQuery, setSearchQuery] = useState('');
//
//     function handleSearch(query) {
//         setSearchQuery(query);
//     }
//
//
//
//     function render(response){
//         const filteredApplications = response.applications.filter((el) =>
//             el.name.toLowerCase().includes(searchQuery.toLowerCase())
//         );
//         function renderList(){
//             if(response.applications.length === 0){
//                 return (
//                     <EmptyList resource={"application"}/>
//                 )
//             } else {
//                 if(filteredApplications.length === 0){
//                     return (
//                         <EmptySearchResult resource={"application"} searchValue={searchQuery} />
//                     )
//                 } else {
//                     return (
//                         <ApplicationsList
//                             applications={filteredApplications}
//                         />
//
//
//                     )
//                 }
//             }
//         }
//
//         return (
//             <>
//                 <ListPageHeader
//                     title={`Applications (${filteredApplications.length})`}
//                     buttonText={""}
//                     hasButton={false}
//                     searchQuery={searchQuery}
//                     handleSearch={handleSearch}
//                 />
//                 <div className={"list-container"}>
//                     {renderList()}
//                 </div>
//             </>
//         )
//     }
//
//     return (
//         <>
//             <Suspense fallback={<LoadingBanner/>}>
//                 <Await resolve={loaderDataPromise.applications}>
//                     {
//                         render
//                     }
//                 </Await>
//             </Suspense>
//         </>
//     )
//
// }
//
// export default Applications
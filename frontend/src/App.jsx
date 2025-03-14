import {
    RouterProvider,
    createBrowserRouter,
    createRoutesFromElements, Route,
} from 'react-router-dom'

import {AuthProvider} from "./AuthContext.jsx";
import ProtectedRoute from "./ProtectedRoute.jsx";

import './App.css'
import RootLayout from "./components/layouts/RootLayout.jsx";
import Home from "./pages/Home.jsx";
import Environments, { loader as environmentLoader } from "./pages/environment/Environments.jsx";
import EnvironmentsCreate, { loader as environmentCreateLoader } from "./pages/environment/EnvironmentsCreate.jsx";
import EnvironmentEdit, { loader as environmentEditLoader } from "./pages/environment/EnvironmentEdit.jsx";
import Projects, { loader as projectsLoader } from "./pages/project/Projects.jsx";
import ProjectsCreate, { loader as projectCreateLoader } from "./pages/project/ProjectsCreate.jsx";
import ProjectEdit, { loader as projectEditLoader } from "./pages/project/ProjectEdit.jsx";
import ProjectLayout, { loader as projectLayoutLoader } from "./components/layouts/ProjectLayout.jsx";
import ProjectOverview, { loader as projectOverviewLoader } from "./pages/project/ProjectOverview.jsx";
import ProjectSettingsLayout from "./components/layouts/ProjectSettingsLayout.jsx";
import InstanceEnvironments, { loader as instanceEnvironmentsLoader } from "./components/ui/Instance/InstanceEnvironments.jsx";
import ProjectApiTokens, { loader as projectApiTokensLoader } from "./components/ui/project/ProjectApiTokens.jsx";
import ProjectMembers, {loader as projectMembersLoader} from "./components/ui/project/ProjectMembers.jsx";
import InstanceMembers, {loader as instanceMembersLoader} from "./components/ui/Instance/InstanceMembers.jsx";
import FeaturesCreate, { loader as featureCreateLoader } from "./pages/toggle/FeaturesCreate.jsx";
import FeaturesEdit, { loader as featureEditLoader } from "./pages/toggle/FeaturesEdit.jsx";
import ContextFields, { loader as contextFieldsLoader } from "./pages/ContextFields.jsx";
import ContextFieldsCreate, { loader as contextFieldsCreateLoader } from "./pages/ContextFieldsCreate.jsx";
import ContextFieldEdit, { loader as contextFieldsEditLoader } from "./pages/ContextFieldEdit.jsx";
import TagTypes, { loader as tagsLoader } from "./pages/TagTypes.jsx";
import TagTypesCreate, { loader as tagsCreateLoader } from "./pages/TagTypesCreate.jsx";
import TagTypesEdit, { loader as tagsEditLoader } from "./pages/TagTypesEdit.jsx";
import FeatureToggleLayout, { loader as featureToggleLayoutLoader } from "./components/layouts/FeatureToggleLayout.jsx";
import FeatureToggleOverview, {loader as  featureToggleOverviewLoader} from "./pages/toggle/FeatureToggleOverview.jsx";
import FeatureTogglePayload from "./pages/toggle/FeatureTogglePayload.jsx";
import FeatureToggles, { loader as featureTogglesLoader } from "./pages/toggle/FeatureToggles.jsx";
import Events, { loader as eventsLoader } from "./pages/Events.jsx";
import NotFound from "./pages/NotFound.jsx";
import Profile from "./pages/Profile.jsx";
import ProfileLayout from "./components/layouts/ProfileLayout.jsx";
import Login from "./pages/Login.jsx";
import CustomToast from "./components/ui/common/CustomToast.jsx";
import Error from "./components/Error.jsx";
import Documentation from "./pages/Documentation.jsx";
import FeatureToggleSchedule,  { loader as scheduleLoader } from "./pages/toggle/FeatureToggleSchedule.jsx";
import InstanceOverview, { loader as instanceOverviewLoader } from "./pages/instance/InstanceOverview.jsx";
import FeatureToggleInstanceLayout, {loader as featureToggleInstanceLayoutLoader} from "./components/layouts/FeatureToggleInstanceLayout.jsx";
import FeatureToggleInstanceOverview,{loader as featureToggleInstanceOverviewLoader} from "./pages/toggle/FeatureToggleInstanceOverview.jsx";
import Instances, { loader as instancesLoader } from "./pages/instance/Instances.jsx";
import InstancesCreate,{loader as instancesCreateLoader} from "./pages/instance/InstancesCreate.jsx";
import InstanceLayout,{loader as instanceLayoutLoader} from "./components/layouts/InstanceLayout.jsx";
import SystemAdminHome from "./pages/systemAdmin/SystemAdminHome.jsx";
import AllAdminsList,{loader as allAdminsLoader} from "./components/ui/systemAdmin/AllAdminsList.jsx";
import AllProjectsList,{loader as allProjectsloader} from "./components/ui/systemAdmin/AllProjectsList.jsx";
import AllInstancesList,{loader as allInstancesLoader} from "./components/ui/systemAdmin/AllInstancesList.jsx";
import SystemAdminLayout from "./components/layouts/SystemAdminLayout.jsx";
import UserInstanceList,{loader as userInstancesLoader} from "./components/ui/systemAdmin/UserInstanceList.jsx";
import UserProjectList,{loader as userProjectsLoader} from "./components/ui/systemAdmin/UserProjectList.jsx";
import ProjectAdminsList,{loader as projectAdminLoader} from "./components/ui/systemAdmin/ProjectAdminsList.jsx";
import InstanceAdminsList,{ loader as instanceAdminLoader} from "./components/ui/systemAdmin/InstanceAdminsList.jsx";
import AddInstanceAdmin, {loader as addInstanceAdminLoader} from "./components/ui/systemAdmin/AddInstanceAdmin.jsx";
import AddProjectAdmin ,{loader as addProjectAdminLoader} from "./components/ui/systemAdmin/AddProjectAdmin.jsx";
import InstanceAdminHome, {loader as instanceAdminHomeLoader} from "./pages/instance/InstanceAdminHome.jsx";

const router = createBrowserRouter(
    createRoutesFromElements(
        [
            <Route element={<ProtectedRoute allowedRoles={["ProjectAdmin", "SystemAdmin", "InstanceAdmin"]} />}>
                <Route path="/" element={<RootLayout />} errorElement={<Error />}>

                    <Route index element={<Home />} />
                    <Route path={"events"} element={<Events />} loader={eventsLoader} errorElement={<Error/>}/>

                    {/* Project Admin Routes */}
                    <Route element={<ProtectedRoute allowedRoles={["ProjectAdmin"]} />}>
                        <Route path="projects" element={<Projects />} loader={projectsLoader} errorElement={<Error />} />
                        <Route path="projects/edit/:projectId" element={<ProjectEdit />} loader={projectEditLoader} errorElement={<Error />} />
                        <Route path="projects/:projectId" element={<ProjectLayout />} loader={projectLayoutLoader} errorElement={<Error />}>
                            <Route index element={<ProjectOverview />} loader={projectOverviewLoader} />
                            <Route path="members" element={<ProjectMembers />} loader={projectMembersLoader} />
                            <Route path={"context"} element={<ContextFields />} loader={contextFieldsLoader} errorElement={<Error/>}/>
                            <Route path={"context/create"} element={<ContextFieldsCreate />} loader={contextFieldsCreateLoader} errorElement={<Error/>}/>
                            <Route path={"context/edit/:contextFieldId"} element={<ContextFieldEdit />} loader={contextFieldsEditLoader} errorElement={<Error/>}/>
                            <Route path={"tags"} element={<TagTypes />} loader={tagsLoader} errorElement={<Error/>}/>
                            <Route path={"tags/create"} element={<TagTypesCreate />} loader={tagsCreateLoader} errorElement={<Error/>}/>
                            <Route path={"tags/edit/:tagId"} element={<TagTypesEdit />} loader={tagsEditLoader} errorElement={<Error/>}/>
                            <Route path={"instances"} element={<Instances />} loader={instancesLoader} errorElement={<Error/>}/>
                        </Route>
                        <Route path={"projects/features"} element={<FeatureToggles />} loader={featureTogglesLoader} errorElement={<Error/>}/>
                        <Route path={"projects/:projectId/features/:featureId"} element={<FeatureToggleLayout/>} loader={featureToggleLayoutLoader} errorElement={<Error/>}>
                            <Route index element={<FeatureToggleOverview/>} errorElement={<Error/>} loader={featureToggleOverviewLoader}/>
                        </Route>

                        <Route path={"projects/:projectId/features/create"} element={<FeaturesCreate />} loader={featureCreateLoader} errorElement={<Error/>}/>
                        <Route path={"projects/:projectId/features/edit/:featureId"} element={<FeaturesEdit />} loader={featureEditLoader} errorElement={<Error/>}/>
                    </Route>

                    {/* System Admin Routes */}
                    <Route element={<ProtectedRoute allowedRoles={["SystemAdmin"]} />}>
                        <Route path="system-admin" element={<SystemAdminLayout />} errorElement={<Error />}>
                            <Route index element={<SystemAdminHome />} errorElement={<Error />} />
                            <Route path="all-admins" element={<AllAdminsList />} loader={allAdminsLoader} errorElement={<Error />} />
                            <Route path={":userId/projects"} element={<UserProjectList />} loader={userProjectsLoader} errorElement={<Error />}/>
                            <Route path={":userId/instances"} element={<UserInstanceList />} loader={userInstancesLoader} errorElement={<Error />}/>

                            <Route path={"all-instances"} element={<AllInstancesList/>} loader={allInstancesLoader} errorElement={<Error/>}/>
                            <Route path={":projectId/instances/:instanceId/instance-admins"} element={<InstanceAdminsList/>} loader={instanceAdminLoader} errorElement={<Error/>}/>
                            <Route path={"instances/:instanceId/create"} element={<AddInstanceAdmin/>} loader={addInstanceAdminLoader} errorElement={<Error/>}/>
                            <Route path={"instances/create/:projectId"} element={<InstancesCreate />} loader={instancesCreateLoader} errorElement={<Error/>}/>

                            <Route path="all-projects" element={<AllProjectsList />} loader={allProjectsloader} errorElement={<Error />} />
                            <Route path={"projects/create"} element={<ProjectsCreate />} loader={projectCreateLoader} errorElement={<Error/>}/>
                            <Route path={"projects/:projectId/create"} element={<AddProjectAdmin/>} loader={addProjectAdminLoader} errorElement={<Error/>}/>
                            <Route path={"projects/:projectId/project-admins"} element={<ProjectAdminsList/>} loader={projectAdminLoader} errorElement={<Error/>}/>

                            {/*system-admin->environments*/}
                            <Route path={"environments"} element={<Environments />} loader={environmentLoader} errorElement={<Error/>}/>
                            <Route path={"environments/create"} element={<EnvironmentsCreate />} loader={environmentCreateLoader} errorElement={<Error/>}/>
                            <Route path={"environments/edit/:envId"} element={<EnvironmentEdit />} loader={environmentEditLoader} errorElement={<Error/>}/>

                        </Route>
                    </Route>

                    {/* Instance Admin Routes */}
                    <Route element={<ProtectedRoute allowedRoles={["InstanceAdmin"]} />}>
                        <Route path="instances" element={<InstanceAdminHome />} loader={instanceAdminHomeLoader} errorElement={<Error />} />
                        <Route path="instances/:instanceId/projects/:projectId" element={<InstanceLayout />} loader={instanceLayoutLoader} errorElement={<Error />}>
                            <Route index element={<InstanceOverview />} loader={instanceOverviewLoader} errorElement={<Error />} />
                            <Route path="members" element={<InstanceMembers />} loader={instanceMembersLoader} />
                            <Route path={"settings"} element={<ProjectSettingsLayout/>}>
                                <Route index element={<InstanceEnvironments/>} loader={instanceEnvironmentsLoader} errorElement={<Error/>}/>
                                <Route path={"api-tokens"} element={<ProjectApiTokens/>} loader={projectApiTokensLoader} errorElement={<Error/>}/>
                            </Route>
                        </Route>
                        <Route path={"/instances/:instanceId/projects/:projectId/features/:featureId"} element={<FeatureToggleInstanceLayout/>} loader={featureToggleInstanceLayoutLoader} errorElement={<Error/>}>
                            <Route index element={<FeatureToggleInstanceOverview/>}  loader={featureToggleInstanceOverviewLoader} errorElement={<Error/>}/>
                            <Route path={"payload"} element={<FeatureTogglePayload/>} errorElement={<Error/>}/>
                            <Route path={"events"} element={<Events/>} loader={eventsLoader} errorElement={<Error/>}/>
                            <Route path={"schedule"} element={<FeatureToggleSchedule/>} loader={scheduleLoader} errorElement={<Error/>}/>
                        </Route>
                    </Route>

                    {/* Common Routes for All Users */}
                    <Route path="profile" element={<ProfileLayout />}>
                        <Route index element={<Profile />} />
                    </Route>

                </Route>
            </Route>,

            <Route path="/documentation" element={<Documentation />} />,
            <Route path="/login" element={<Login />} />,
            <Route path="*" element={<NotFound />} />,
        ]
    )
);


function App() {
  return (
      <AuthProvider>
        <RouterProvider router={router} />
        <CustomToast/>
      </AuthProvider>
  )
}

export default App
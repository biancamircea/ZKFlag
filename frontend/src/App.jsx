import {
    RouterProvider,
    createBrowserRouter,
    createRoutesFromElements, Route,
} from 'react-router-dom'

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
import ProjectMembers from "./components/ui/project/ProjectMembers.jsx";
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
import Applications, { loader as applicationsLoader } from "./pages/application/Applications.jsx";
import ApplicationsLayout, { loader as applicationLayoutLoader } from "./components/layouts/ApplicationsLayout.jsx";
import ApplicationOverview from "./pages/application/ApplicationOverview.jsx";
import ApplicationEdit from "./pages/application/ApplicationEdit.jsx";
import Events, { loader as eventsLoader } from "./pages/Events.jsx";
import NotFound from "./pages/NotFound.jsx";
import Profile from "./pages/Profile.jsx";
import ProfileLayout from "./components/layouts/ProfileLayout.jsx";
import ProfileSettings from "./pages/ProfileSettings.jsx";
import Login from "./pages/Login.jsx";
import CustomToast from "./components/ui/common/CustomToast.jsx";
import Error from "./components/Error.jsx";
import Documentation from "./pages/Documentation.jsx";
import FeatureToggleSchedule,  { loader as scheduleLoader } from "./pages/toggle/FeatureToggleSchedule.jsx";
import InstanceOverview, { loader as instanceOverviewLoader } from "./pages/instance/InstanceOverview.jsx";
import FeatureToggleInstanceLayout from "./components/layouts/FeatureToggleInstanceLayout.jsx";
import FeatureToggleInstanceOverview from "./pages/toggle/FeatureToggleInstanceOverview.jsx";
import Instances, { loader as instancesLoader } from "./pages/instance/Instances.jsx";
import InstancesCreate,{loader as instancesCreateLoader} from "./pages/instance/InstancesCreate.jsx";
import InstanceLayout,{loader as instanceLayoutLoader} from "./components/layouts/InstanceLayout.jsx";

const router = createBrowserRouter(createRoutesFromElements(
    [
        <Route
            path="/"
            element={<RootLayout />}
            errorElement={<Error/>}>
            <Route index element={<Home />} />
            <Route
                path={"projects"}
                element={<Projects />}
                loader={projectsLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"projects/create"}
                element={<ProjectsCreate />}
                loader={projectCreateLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"projects/edit/:projectId"}
                element={<ProjectEdit />}
                loader={projectEditLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"projects/:projectId"}
                element={<ProjectLayout />}
                loader={projectLayoutLoader}
                errorElement={<Error/>}
            >
                <Route
                    index
                    element={<ProjectOverview/>}
                    loader={projectOverviewLoader}
                />
                <Route
                    path={"context"}
                    element={<ContextFields />}
                    loader={contextFieldsLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"context/create"}
                    element={<ContextFieldsCreate />}
                    loader={contextFieldsCreateLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"context/edit/:contextFieldId"}
                    element={<ContextFieldEdit />}
                    loader={contextFieldsEditLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"tags"}
                    element={<TagTypes />}
                    loader={tagsLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"tags/create"}
                    element={<TagTypesCreate />}
                    loader={tagsCreateLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"tags/edit/:tagId"}
                    element={<TagTypesEdit />}
                    loader={tagsEditLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"instances"}
                    element={<Instances />}
                    loader={instancesLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"instances/create"}
                    element={<InstancesCreate />}
                    loader={instancesCreateLoader}
                    errorElement={<Error/>}
                />
            </Route>
            <Route
                path={"/projects/:projectId/instances/:instanceId"}
                element={<InstanceLayout />}
                loader={instanceLayoutLoader}
                errorElement={<Error/>}
            >
                <Route
                    index
                    element={<InstanceOverview/>}
                    loader={instanceOverviewLoader}
                    errorElement={<Error/>}
                />
                <Route path={"settings"} element={<ProjectSettingsLayout/>}>
                    // TODO aici voi modifica cu instance
                    <Route
                        index
                        element={<InstanceEnvironments/>}
                        loader={instanceEnvironmentsLoader}
                        errorElement={<Error/>}
                    />
                    <Route path={"members"} element={<ProjectMembers/>}/>
                    <Route
                        path={"api-tokens"}
                        element={<ProjectApiTokens/>}
                        loader={projectApiTokensLoader}
                        errorElement={<Error/>}
                    />
                </Route>
                <Route
                    path={"events"}
                    element={<Events />}
                    loader={eventsLoader}
                    errorElement={<Error/>}
                />
            </Route>
            <Route
                path={"features"}
                element={<FeatureToggles />}
                loader={featureTogglesLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"projects/:projectId/features/:featureId"}
                element={<FeatureToggleLayout/>}
                loader={featureToggleLayoutLoader}
                errorElement={<Error/>}
            >
                <Route
                    index
                    element={<FeatureToggleOverview/>}
                    errorElement={<Error/>}
                    loader={featureToggleOverviewLoader}
                />
            </Route>
            <Route
                path={"projects/:projectId/instances/:instanceId/features/:featureId"}
                element={<FeatureToggleInstanceLayout/>}
                loader={featureToggleLayoutLoader}
                errorElement={<Error/>}
            >
                <Route
                    index
                    element={<FeatureToggleInstanceOverview/>}
                    errorElement={<Error/>}
                />
                <Route
                    path={"payload"}
                    element={<FeatureTogglePayload/>}
                    errorElement={<Error/>}
                />
                <Route
                    path={"events"}
                    element={<Events/>}
                    loader={eventsLoader}
                    errorElement={<Error/>}
                />
                <Route
                    path={"schedule"}
                    element={<FeatureToggleSchedule/>}
                    loader={scheduleLoader}
                    errorElement={<Error/>}
                />
            </Route>
            <Route
                path={"projects/:projectId/features/create"}
                element={<FeaturesCreate />}
                loader={featureCreateLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"projects/:projectId/features/edit/:featureId"}
                element={<FeaturesEdit />}
                loader={featureEditLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"environments"}
                element={<Environments />}
                loader={environmentLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"environments/create"}
                element={<EnvironmentsCreate />}
                loader={environmentCreateLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"environments/edit/:envId"}
                element={<EnvironmentEdit />}
                loader={environmentEditLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"applications"}
                element={<Applications />}
                loader={applicationsLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"applications/:appId"}
                element={<ApplicationsLayout />}
                loader={applicationLayoutLoader}
                errorElement={<Error/>}
            >
                <Route
                    index
                    element={<ApplicationOverview/>}
                    errorElement={<Error/>}
                />
                <Route
                    path={"edit"}
                    element={<ApplicationEdit/>}
                    errorElement={<Error/>}
                />
            </Route>
            <Route
                path={"events"}
                element={<Events />}
                loader={eventsLoader}
                errorElement={<Error/>}
            />
            <Route
                path={"users"}
                element={<Events />}
                errorElement={<Error/>}
            />
            <Route path={"profile"} element={<ProfileLayout />}>
                <Route index element={<Profile />} />
                <Route path={"settings"} element={<ProfileSettings />} />
            </Route>
        </Route>,
        <Route path={"/documentation"} element={<Documentation/>}/>,
        <Route path={"/login"} element={<Login/>}/>,
        <Route path="*" element={<NotFound />} />
    ]
))

function App() {
  return (
      <>
        <RouterProvider router={router} />
        <CustomToast/>
      </>
  )
}

export default App
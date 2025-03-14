import React, { createContext, useContext, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, Outlet } from "react-router-dom";
import { useDepartment } from "../DepartmentProvider/DepartmentProvider";
import Home from "../../Pages/Home/Home";
import Login from "../../Pages/Login/Login";
import PgAdmin from "../../Pages/PgAdmin/PgAdmin";
import Requests from "../../Pages/Requests/Requests";
import RequestsForSign from "../../Pages/RequestsForSign/RequestsForSign";
import RequestForSign from "../../Pages/RequestForSign/RequestForSign";
import CreateRequest from "../../Pages/CreateRequest/CreateRequest";
import UserManagement from "../../Pages/UserManagement/UserManagement";
import ModifyCommander  from "../../Pages/ModifyCommander/ModifyCommander";
import ModifyHeadDep from "../../Pages/ModifyHeadDep/ModifyHeadDep";
import ModifyUserDep from "../../Pages/ModifyUserDep/ModifyUserDep";
import CreateUser from "../../Pages/CreateUser/CreateUser";
import CreateDepartment from "../../Pages/CreateDepartament/CreateDepartment";
import SignedRequest from "../../Pages/SignedRequest/SignedRequest";
import DepartmentProvider from "../DepartmentProvider/DepartmentProvider";
import "./styleRouter.css";
import Navbar from "../Navbar/Navbar";
import AdminNavbar from "../AdminNavbar/AdminNavbar";
import { useLocation } from "react-router-dom";

const AuthContext = createContext(null);

export const useAuth = () => {
    return useContext(AuthContext);
};

const Layout = ({ isDark }) => {
    const { departmentColor } = useDepartment();
    const location = useLocation();
    const { role } = useAuth();

    const isLoginPage = location.pathname === "/login";

    return (
        <div
            style={{
                backgroundColor: isDark ? "black" : departmentColor || "darkseagreen",
            }}
            className="totRouter"
        >
            {!isLoginPage && (role === "ROLE_ADMIN" ? <AdminNavbar /> : <Navbar />)}

            <Outlet />
        </div>
    );
};


const PrivateRoute = ({children, allowedRoles}) => {

    const isAuthenticated = !!localStorage.getItem("isAuthenticated");
    const userRole = localStorage.getItem("role");
    const isAuthorized = allowedRoles ? allowedRoles.includes(userRole) : true;

    return isAuthenticated && isAuthorized ? children : <Navigate to="/login" />;

};



const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(
        () => localStorage.getItem("isAuthenticated") === "true"
    );
    const [role, setRole] = useState(
        () => localStorage.getItem("role") || null
    );



    const login = (role) => {
        setIsAuthenticated(true);
        setRole(role);
        localStorage.setItem("role", role);
        localStorage.setItem("isAuthenticated", "true");
    };

    const logout = () => {
        setIsAuthenticated(false);
        setRole(null);
        localStorage.removeItem("isAuthenticated");
        localStorage.removeItem("role");
        localStorage.removeItem("idUser");
        localStorage.removeItem("token");
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, role, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};



const BrowserRoutes = ({ isDark }) => {
    const auth = useAuth();

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Layout isDark={isDark} />}>
                    <Route
                        index
                        element={<Navigate to={auth.isAuthenticated ? "/home" : "/login"} />}
                    />
                    <Route path="login" element={<Login />} />
                    <Route
                        path="home"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_EMPLOYEE"]}>
                                <Home />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="requests"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_EMPLOYEE"]}>
                                <Requests />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="create_request"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_EMPLOYEE"]}>
                                <CreateRequest />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="requests_for_sign"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_EMPLOYEE"]}>
                                <RequestsForSign />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="pgAdmin"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
                                <PgAdmin />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="user_managemnt"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
                                <UserManagement />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="mod_commander"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
                                <ModifyCommander />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="mod_head dep"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
                                <ModifyHeadDep />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="mod_user_dep"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
                                <ModifyUserDep />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="create_user"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
                                <CreateUser />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="create_dep"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_ADMIN"]}>
                                <CreateDepartment />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="request_for_sign/:requestId"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_EMPLOYEE"]}>
                                <RequestForSign />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="signed_request"
                        element={
                            <PrivateRoute allowedRoles={["ROLE_EMPLOYEE"]}>
                                <SignedRequest />
                            </PrivateRoute>
                        }
                    />
                    <Route path="*" element={<Navigate to="/home" />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
};


export default function Router({ isDark } ) {
    return (
        <DepartmentProvider>
            <AuthProvider>
                <BrowserRoutes isDark={isDark} />
            </AuthProvider>
        </DepartmentProvider>
    );
}


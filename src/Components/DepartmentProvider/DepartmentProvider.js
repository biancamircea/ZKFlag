import React, { createContext, useContext, useState, useEffect } from "react";
import { FeatureFlagContext } from "../FeatureFlags/FeatureFlagContext";
import axios from "axios";

const DepartmentContext = createContext(null);

export const useDepartment = () => {
    return useContext(DepartmentContext);
};

const DepartmentProvider = ({ children }) => {
    const [departmentName, setDepartmentName] = useState(null);
    const [departmentColor, setDepartmentColor] = useState(null);
    const { featureFlags, checkFeature } = useContext(FeatureFlagContext);

    useEffect(() => {
        const userId = localStorage.getItem("idUser");

        if (userId) {

            const fetchDepartmentName = async () => {
                try {
                    const response = await axios.get(`http://localhost:9090/api/v1/employees/get-departament-name/${userId}`);
                    setDepartmentName(response.data);
                } catch (error) {
                    console.error("Error fetching department name:", error);
                    setDepartmentName(null);
                }
            };

            fetchDepartmentName();
        }
    }, []);

    useEffect(() => {
        if (!departmentName) return;

        const fetchFeatureStatus = async () => {
            const featureName = "background-color";
            const contextFields = [{ name: "user_role", value: "25" },{ name: "conf", value:"14" }];
            const featureData = await checkFeature(featureName, contextFields);
            setDepartmentColor(featureData.payload);
        };
        fetchFeatureStatus();
    }, [departmentName, checkFeature]);

    return (
        <DepartmentContext.Provider value={{ departmentName, departmentColor, setDepartmentName }}>
            {children}
        </DepartmentContext.Provider>
    );
};

export default DepartmentProvider;
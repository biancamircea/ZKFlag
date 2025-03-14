import React, { createContext, useContext, useState, useEffect } from "react";
import {FeatureFlagContext} from "../FeatureFlagContext";

const DepartmentContext = createContext(null);

export const useDepartment = () => {
    return useContext(DepartmentContext);
};

const DepartmentProvider = ({ children }) => {
    const [departmentName, setDepartmentName] = useState(null);
    const [departmentColor, setDepartmentColor] = useState(null);
    const { featureFlags, checkFeature } = useContext(FeatureFlagContext);

    useEffect(() => {
        const fetchFeatureStatus = async () => {
            const featureName = "backgroundColor";
            const contextFields = [{ name: "port", value: `${process.env.REACT_APP_API_PORT}` }];
            const featureData = await checkFeature(featureName, contextFields);

            setDepartmentColor(featureData.payload || "darkseagreen");
        };

        fetchFeatureStatus();
    }, [checkFeature]);



    return (
        <DepartmentContext.Provider value={{ departmentName, departmentColor }}>
            {children}
        </DepartmentContext.Provider>
    );
};

export default DepartmentProvider;
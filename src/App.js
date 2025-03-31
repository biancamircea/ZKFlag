import React, { useEffect, useState, useContext } from "react";
import { FeatureFlagContext, FeatureFlagProvider } from "./Components/FeatureFlags/FeatureFlagContext";
import Router from "./Components/Router/Router";
import "bootstrap/dist/css/bootstrap.min.css";
import { Toggle } from "./Components/Toggle";

const App = () => {
    const { isFeatureFlagEnabled } = useContext(FeatureFlagContext) || { isFeatureFlagEnabled: false };

    const [isDark, setIsDark] = useState(() => {
        return localStorage.getItem("isDark") === "true";
    });

    useEffect(() => {
        localStorage.setItem("isDark", isDark);
    }, [isDark]);

    return (
        <div className="App" data-theme={isDark ? "dark" : "light"}>
            {isFeatureFlagEnabled && (
                <Toggle isChecked={isDark} handleChange={() => setIsDark(!isDark)} />
            )}
            <Router isDark={isDark} />
        </div>
    );
};

const AppWithProviders = () => (
    <FeatureFlagProvider>
        <App />
    </FeatureFlagProvider>
);

export default AppWithProviders;

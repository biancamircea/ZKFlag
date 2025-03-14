import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { fetchUser } from "../api/userApi";

function Home() {
    const navigate = useNavigate();

    useEffect(() => {
        const redirectUser = async () => {
            try {
                const userData = await fetchUser();
                switch (userData.role) {
                    case "SystemAdmin":
                        navigate("/system-admin");
                        break;
                    case "ProjectAdmin":
                        navigate("/projects");
                        break;
                    case "InstanceAdmin":
                        navigate("/instances");
                        break;
                    default:
                        navigate("/");
                }
            } catch (error) {
                console.error("Failed to fetch user data:", error);
                navigate("/login");
            }
        };

        redirectUser();
    }, [navigate]);

    return (
        <>
            <h1>This is Home page!</h1>
        </>
    );
}

export default Home;
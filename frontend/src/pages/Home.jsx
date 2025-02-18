import {useNavigate} from "react-router-dom";
import {useEffect} from "react";

function Home() {
    const navigate = useNavigate();

    useEffect(() => {
        navigate("/projects")
    }, []);

    return (
        <>
            <h1>This is Home page!</h1>
        </>
    )
}
export default Home
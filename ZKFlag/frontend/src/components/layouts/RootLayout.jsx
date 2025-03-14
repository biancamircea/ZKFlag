import Header from "../Header.jsx";
import Footer from "../Footer.jsx";
import {Outlet} from "react-router-dom";


function RootLayout() {
    return (
        <div className={"layout-container"}>
            <Header />
            <main>
                <Outlet />
            </main>
            <Footer />
        </div>
    )
}

export default RootLayout
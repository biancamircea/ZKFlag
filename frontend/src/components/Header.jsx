import {Link, NavLink} from "react-router-dom";
import HeaderDropDown from "./ui/common/HeaderDropDown.jsx";
import Tooltip from "@mui/material/Tooltip";

function Header() {

    return (
        <header>
            <div className={"header-navlinks-container"}>
                <Link to={"/"} className={"header-container"}>
                    <img src={"/src/assets/images/favicon.png"} className={"header-logo"} alt={"favorite-icon"}/>
                    <span className={"header-title"}>Toggle System</span>
                </Link>
                <NavLink to={"projects"}>
                    Projects
                </NavLink>
                <NavLink to={"features"}>
                    Feature Toggles
                </NavLink>
                <NavLink to={"environments"}>
                    Environments
                </NavLink>
            </div>
            <div className={"header-icons-container"}>
                <Tooltip title={"Check documentation"} arrow>
                    <Link to={"/documentation"} className={"icon-with-arrow"} target={"_blank"}>
                        <img src="/src/assets/images/document.png" alt={"document"} className={"small-icon"}/>
                    </Link>
                </Tooltip>
                <HeaderDropDown
                    title={(<img src="/src/assets/images/settings.png" alt={"settings"} className={"small-icon"}/>)}
                    items={[
                        "applications",
                        "events",
                    ]}
                    profile={false}
                />
                <HeaderDropDown
                    title={(<img src="/src/assets/images/user.png" alt={"settings"} className={"profile-icon"}/>)}
                    items={[]}
                    profile={true}
                />
            </div>
        </header>
    )
}

export default Header
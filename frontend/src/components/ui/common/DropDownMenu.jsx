import {NavLink} from "react-router-dom";

export default function DropDownMenu(props) {
    const {elements: elementsArray, id} = props
    const navElements = elementsArray.map((el, index) => {
        return (

            <NavLink to={`${el}`} key={index}>
                <div className={"drop-down-item"}>
                    {el.charAt(0).toUpperCase() + el.slice(1)}
                </div>
            </NavLink>
        )
    })
    return (
            <div className={"drop-down-menu"} id={id}>
                {navElements}
            </div>
    )


}
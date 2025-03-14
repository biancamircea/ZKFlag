import React, {useEffect, useRef, useState} from 'react';
import DropDownMenu from "./DropDownMenu.jsx";
import DropDownProfile from "./DropDownProfile.jsx";

function HeaderDropDown(props) {
    const {title, items, profile, user} = props
    const [shown, setShown] = useState(false)
    const ref = useRef(null);
    function toggleState(){
        setShown(prevState => !prevState)
    }

    useEffect(() => {
        document.addEventListener('click', handleClickOutside)
        return () => {
            document.removeEventListener('click', handleClickOutside)
        }
    }, [])

    function handleClickOutside(event) {
        if (ref.current && !ref.current.contains(event.target)) {
            setShown(false)
        }
    }

    return (
        <div className={"header-navlink-el"}
             onClick={toggleState}
             onMouseDown={handleClickOutside}
             ref={ref}
        >
            <div className={"icon-with-arrow"}>
                {title}
                <img src="/images/down-arrow.png" alt={"settings"} className={"smaller-icon"}/>
            </div>
            {
                shown &&
                (
                    profile ?
                        <DropDownProfile
                            id={"drop-down"}
                            user={user}
                        />
                        :
                        <DropDownMenu
                            elements={items}
                            id={"drop-down"}
                        />
                )
            }
        </div>
    );
}

export default HeaderDropDown;
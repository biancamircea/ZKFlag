import React, {useEffect, useRef, useState} from 'react';
import DropDownMenu from "./DropDownMenu.jsx";
import DropDownProfile from "./DropDownProfile.jsx";
function HeaderDropDown(props) {
    const {title, items, profile} = props
    const [shown, setShown] = useState(false)
    const ref = useRef(null);
    function toggleState(){
        setShown(prevState => !prevState)
    }

    useEffect(() => {
        // Add event listener to the document object
        document.addEventListener('click', handleClickOutside)
        // Remove event listener on component unmount
        return () => {
            document.removeEventListener('click', handleClickOutside)
        }
    }, [])

    function handleClickOutside(event) {
        // Check if the clicked element is outside the component
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
                <img src="/src/assets/images/down-arrow.png" alt={"settings"} className={"smaller-icon"}/>
            </div>
            {
                shown &&
                (
                    profile ?
                        <DropDownProfile
                            id={"drop-down"}
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
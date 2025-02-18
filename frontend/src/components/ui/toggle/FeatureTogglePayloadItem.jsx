import React from 'react';
import FeatureTogglePayloadContainer from "./FeatureTogglePayloadContainer.jsx";
import DeleteBtnDialog from "../common/DeleteBtnDialog.jsx";
import FeatureTogglePayloadBtn from "./FeatureTogglePayloadBtn.jsx";

function FeatureTogglePayloadItem({environmentName, enabledValue, disabledValue, addHandler, updateHandler, deleteHandler}) {
    const hasPayload = (enabledValue != null && disabledValue != null)

    return (
        <div className={"toggle-environment-constraints-container"}>
            <div className={"header"}>
                <img src={"/src/assets/images/environment.png"}
                     alt={"Environment"}
                     className={"environment-icon"}
                />
                <h3>{environmentName}</h3>
            </div>
            {
                hasPayload
                    ? (
                        <FeatureTogglePayloadContainer
                            enabledValue={enabledValue}
                            disabledValue={disabledValue}
                        />)
                    : (
                        <h4>No payload defined</h4>
                    )
            }
            <div className={"payload-buttons"}>
                {
                    hasPayload
                        ? (
                            <>
                                <FeatureTogglePayloadBtn
                                    submitHandler={updateHandler}
                                    defaultValueOn={enabledValue}
                                    defaultValueOff={disabledValue}
                                    edit={true}
                                />
                                <DeleteBtnDialog
                                    btnLabel={"Delete payload"}
                                    style={{background: '#ab0000'}}
                                    resource={"payload"}
                                    resourceName={`payload in environment ${environmentName}`}
                                    deleteHandler={deleteHandler}
                                />
                            </>
                        )
                        : (<FeatureTogglePayloadBtn
                            submitHandler={addHandler}
                        />)
                }
            </div>
        </div>
    );
}

export default FeatureTogglePayloadItem;
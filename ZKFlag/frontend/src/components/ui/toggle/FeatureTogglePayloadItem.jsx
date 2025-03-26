import React from 'react';
import FeatureTogglePayloadContainer from "./FeatureTogglePayloadContainer.jsx";
import DeleteBtnDialog from "../common/DeleteBtnDialog.jsx";
import FeatureTogglePayloadBtn from "./FeatureTogglePayloadBtn.jsx";
import {toast} from "react-toastify";


function FeatureTogglePayloadItem({environmentName, enabledValue, disabledValue, addHandler, updateHandler, deleteHandler}) {
    const hasPayload = (enabledValue != null || disabledValue != null)

    const deleteFile = async (fileUrl) => {
        try {
            const response = await fetch(`/api/minio/delete?fileUrl=${encodeURIComponent(fileUrl)}`, {
                method: "DELETE",
                credentials: "include",
            });

            if (!response.ok) {
                throw new Error("File deletion failed");
            }

        } catch (error) {
            toast.error(error.message);
        }
    };

    const isImageUrl = (value) => {
        return value && (value.endsWith('.jpg') || value.endsWith('.jpeg') || value.endsWith('.png') || value.endsWith('.gif'));
    };

    const handleDelete = async () => {
        if (isImageUrl(enabledValue)) {
            await deleteFile(enabledValue);
        }
        if (isImageUrl(disabledValue)) {
            await deleteFile(disabledValue);
        }

        deleteHandler();
    };


    return (
        <div className={"toggle-environment-constraints-container"}>
            <div className={"header"}>
                <img src={"/images/environment.png"}
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
                                    deleteHandler={handleDelete}
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
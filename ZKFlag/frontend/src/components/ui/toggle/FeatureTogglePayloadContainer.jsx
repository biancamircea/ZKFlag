import React, { useState } from 'react';
import '../../../index.css';

function FeatureTogglePayloadContainer({ enabledValue, disabledValue }) {
    const [isImageModalOpen, setIsImageModalOpen] = useState(false);
    const [imageSrc, setImageSrc] = useState('');

    const openImageModal = (imageUrl) => {
        setImageSrc(imageUrl);
        setIsImageModalOpen(true);
    };


    const closeImageModal = () => {
        setIsImageModalOpen(false);
        setImageSrc('');
    };


    const isImageUrl = (value) => {
        return value && (value.endsWith('.jpg') || value.endsWith('.jpeg') || value.endsWith('.png') || value.endsWith('.gif'));
    };

    const handleModalClick = (e) => {
        if (e.target === e.currentTarget) {
            closeImageModal();
        }
    };

    return (
        <div className="payload-container">
            <div className="payload-value">
                {enabledValue && isImageUrl(enabledValue) ? (
                    <p>
                        <span className={"bold-text large-text"}>ON: </span>
                        <button onClick={() => openImageModal(enabledValue)}>
                            Click to see the image
                        </button>
                    </p>
                ) : (
                    <div><span className={"bold-text large-text"}>ON</span>: {enabledValue}</div>
                )}
            </div>

            <div className="payload-value">
                {disabledValue && isImageUrl(disabledValue) ? (
                    <p>
                        <span className={"bold-text large-text"}>OFF: </span>
                        <button onClick={() => openImageModal(disabledValue)}>
                            Click to see the image
                        </button>
                    </p>
                ) : (
                    <div><span className={"bold-text large-text"}>OFF</span>: {disabledValue}</div>
                )}
            </div>

            {isImageModalOpen && (
                <div className="image-modal" onClick={handleModalClick}>
                    <div className="modal-content">
                        <span className="close-btn" onClick={closeImageModal}>&times;</span>
                        <img src={imageSrc} alt="Payload Image" className="modal-image" />
                    </div>
                </div>
            )}
        </div>
    );
}

export default FeatureTogglePayloadContainer;

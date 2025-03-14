import React from 'react';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
function CustomToast() {
    return (
        <ToastContainer
            position="bottom-center"
            autoClose={2000}
            hideProgressBar={true}
            newestOnTop={false}
            closeOnClick
            rtl={false}
            draggable
            theme="light"
            // pauseOnFocusLoss
            // pauseOnHover
        />
    );
}

export default CustomToast;
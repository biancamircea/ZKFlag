import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import TimePicker from 'react-time-picker';
import Modal from 'react-modal';

Modal.setAppElement('#root'); // Set the root element for accessibility

function SchedulePopup({ isOpen, onClose, type }) {
    const [startDate, setStartDate] = useState(new Date());
    const [startTime, setStartTime] = useState('10:00');
    const [endDate, setEndDate] = useState(new Date());
    const [endTime, setEndTime] = useState('10:00');

    const handleSubmit = () => {
        const now = new Date();

        // Combine date and time into a single Date object
        const combineDateTime = (date, time) => {
            const [hours, minutes] = time.split(':').map(Number);
            const combinedDate = new Date(date);
            combinedDate.setHours(hours, minutes, 0, 0);
            return combinedDate;
        };

        if (type === 'activate' || type === 'deactivate') {
            const selectedDateTime = combineDateTime(startDate, startTime);

            if (selectedDateTime <= now) {
                alert('The selected date and time must be in the future.');
                return;
            }
        }

        if (type === 'interval') {
            if (!startDate || !startTime || !endDate || !endTime) {
                alert('All fields (start date, start time, end date, end time) are required.');
                return;
            }

            const activationDateTime = combineDateTime(startDate, startTime);
            const deactivationDateTime = combineDateTime(endDate, endTime);

            if (activationDateTime <= now) {
                alert('The activation date and time must be in the future.');
                return;
            }

            if (activationDateTime >= deactivationDateTime) {
                alert('The activation date and time must be before the deactivation date and time.');
                return;
            }
        }

        // If all validations pass, close the modal
        onClose();
    };

    const renderContent = () => {
        switch (type) {
            case 'activate':
                return (
                    <div>
                        <h2>Choose moment of activation</h2>
                        <DatePicker selected={startDate} onChange={(date) => setStartDate(date)} />
                        <TimePicker onChange={setStartTime} value={startTime} />
                    </div>
                );
            case 'deactivate':
                return (
                    <div>
                        <h2>Choose moment of deactivation</h2>
                        <DatePicker selected={startDate} onChange={(date) => setStartDate(date)} />
                        <TimePicker onChange={setStartTime} value={startTime} />
                    </div>
                );
            case 'interval':
                return (
                    <div>
                        <h2>Choose activation and deactivation interval</h2>
                        <div>
                            <div>
                                <DatePicker selected={startDate} onChange={(date) => setStartDate(date)} />
                                <TimePicker onChange={setStartTime} value={startTime} />
                            </div>
                            <div>
                                <DatePicker selected={endDate} onChange={(date) => setEndDate(date)} />
                                <TimePicker onChange={setEndTime} value={endTime} />
                            </div>
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <Modal isOpen={isOpen} onRequestClose={onClose} contentLabel="Schedule Popup">
            {renderContent()}
            <button onClick={handleSubmit}>Submit</button>
            <button onClick={onClose}>Close</button>
        </Modal>
    );
}

export default SchedulePopup;

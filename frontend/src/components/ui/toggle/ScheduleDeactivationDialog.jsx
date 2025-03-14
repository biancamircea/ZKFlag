import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { Form } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';


function ScheduleDeactivationDialog({ onClose, open, onSave }) {
    const [stopDate, setStopDate] = useState(null);
    const [stopHour, setStopHour] = useState("");
    const [stopMinute, setStopMinute] = useState("");

    const handleClose = () => {
        onClose();
    };

    function handleSubmit(event) {
        event.preventDefault();

        if (!stopDate && (!stopHour || !stopMinute)) {
            toast.error("Please fill at least one field (Date or Time)!");
            return;
        }

        const now = new Date();

        if (stopDate && stopHour && stopMinute) {
            const selectedDateTime = new Date(stopDate);
            selectedDateTime.setHours(parseInt(stopHour, 10), parseInt(stopMinute, 10), 0, 0);

            if (selectedDateTime < now) {
                toast.error("The selected date and time must not be in the past!");
                return;
            }
        }

        const formattedDate = stopDate
            ? stopDate.toLocaleDateString("en-GB")
            : null;
        const formattedTime =
            stopHour && stopMinute
                ? `${stopHour}:${stopMinute}`
                : null;

        onSave({
            endDate: formattedDate,
            endTime: formattedTime,
        });

        handleClose();
    }

    return (
        <Dialog
            onClose={handleClose}
            open={open}
            className="schedule-dialog"
            maxWidth="sm"
            fullWidth
        >
            <DialogTitle className="schedule-dialog-title">Schedule Deactivation</DialogTitle>
            <Form method="post" onSubmit={handleSubmit}>
                <DialogContent className="schedule-dialog-content">
                    <div className="schedule-dialog-fields">
                        <div className="schedule-dialog-field-item">
                            <label htmlFor="stopDate">Stop Date:</label>
                            <DatePicker
                                selected={stopDate}
                                onChange={(date) => setStopDate(date)}
                                className="schedule-dialog-datepicker"
                                dateFormat="dd/MM/yyyy"
                                isClearable
                                placeholderText="Select a date (optional)"
                            />
                        </div>
                        <div className="schedule-dialog-field-item">
                            <label>Stop Time:</label>
                            <div className="schedule-dialog-time-container">
                                <select
                                    value={stopHour}
                                    onChange={(e) => setStopHour(e.target.value)}
                                    className="schedule-dialog-timepicker"
                                >
                                    <option value="">HH</option>
                                    {Array.from({ length: 24 }, (_, i) => (
                                        <option key={i} value={i < 10 ? `0${i}` : `${i}`}>
                                            {i < 10 ? `0${i}` : `${i}`}
                                        </option>
                                    ))}
                                </select>
                                :
                                <select
                                    value={stopMinute}
                                    onChange={(e) => setStopMinute(e.target.value)}
                                    className="schedule-dialog-timepicker"
                                >
                                    <option value="">MM</option>
                                    {Array.from({ length: 60 }, (_, i) => (
                                        <option key={i} value={i < 10 ? `0${i}` : `${i}`}>
                                            {i < 10 ? `0${i}` : `${i}`}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>
                </DialogContent>
                <DialogActions className="schedule-dialog-actions">
                    <button className="schedule-dialog-cancel-btn" onClick={handleClose}>
                        Cancel
                    </button>
                    <button type="submit" className="schedule-dialog-save-btn">
                        Save
                    </button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default ScheduleDeactivationDialog;

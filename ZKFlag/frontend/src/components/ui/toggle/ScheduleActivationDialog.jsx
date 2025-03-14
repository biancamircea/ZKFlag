import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { Form } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';


function ScheduleActivationDialog({ onClose, open, onSave }) {
    const [startDate, setStartDate] = useState(null);
    const [startHour, setStartHour] = useState("");
    const [startMinute, setStartMinute] = useState("");

    const handleClose = () => {
        onClose();
    };

    function handleSubmit(event) {
        event.preventDefault();

        if (!startDate && (!startHour || !startMinute)) {
            toast.error("Please fill at least one field (Date or Time)!");
            return;
        }

        const now = new Date();
        if (startDate && startHour && startMinute) {
            const selectedDateTime = new Date(startDate);
            selectedDateTime.setHours(parseInt(startHour, 10), parseInt(startMinute, 10), 0, 0);

            if (selectedDateTime < now) {
                toast.error("The selected date and time must not be in the past!");
                return;
            }
        }

        const formattedDate = startDate
            ? startDate.toLocaleDateString("en-GB") // Format: DD/MM/YYYY
            : null;
        const formattedTime =
            startHour && startMinute
                ? `${startHour}:${startMinute}`
    : null;

        onSave({ startDate: formattedDate, startTime: formattedTime });
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
            <DialogTitle className="schedule-dialog-title">Schedule Activation</DialogTitle>
            <Form method="post" onSubmit={handleSubmit}>
                <DialogContent className="schedule-dialog-content">
                    <div className="schedule-dialog-fields">
                        <div className="schedule-dialog-field-item">
                            <label htmlFor="startDate">Start Date:</label>
                            <DatePicker
                                selected={startDate}
                                onChange={(date) => setStartDate(date)}
                                className="schedule-dialog-datepicker"
                                dateFormat="dd/MM/yyyy"
                                isClearable
                                placeholderText="Select a date (optional)"
                            />
                        </div>
                        <div className="schedule-dialog-field-item">
                            <label>Start Time:</label>
                            <div className="schedule-dialog-time-container">
                                <select
                                    value={startHour}
                                    onChange={(e) => setStartHour(e.target.value)}
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
                                    value={startMinute}
                                    onChange={(e) => setStartMinute(e.target.value)}
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

export default ScheduleActivationDialog;
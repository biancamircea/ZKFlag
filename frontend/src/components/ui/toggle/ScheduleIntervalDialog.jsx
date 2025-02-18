import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { Form } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import './styles/ScheduleIntervalDialog.css';

function ScheduleIntervalDialog({ onClose, open, onSave }) {
    const [startDate, setStartDate] = useState(null);
    const [startHour, setStartHour] = useState("");
    const [startMinute, setStartMinute] = useState("");
    const [endDate, setEndDate] = useState(null);
    const [endHour, setEndHour] = useState("");
    const [endMinute, setEndMinute] = useState("");

    const handleClose = () => {
        onClose();
    };

    function handleSubmit(event) {
        event.preventDefault();

        if (!startDate || !startHour || !startMinute || !endDate || !endHour || !endMinute) {
            toast.error("All fields are required!");
            return;
        }

        const now = new Date();

        const startDateTime = new Date(startDate);
        startDateTime.setHours(parseInt(startHour, 10), parseInt(startMinute, 10), 0, 0);

        const endDateTime = new Date(endDate);
        endDateTime.setHours(parseInt(endHour, 10), parseInt(endMinute, 10), 0, 0);

        if (startDateTime < now) {
            toast.error("Start date and time must not be in the past!");
            return;
        }

        if (endDateTime < now) {
            toast.error("End date and time must not be in the past!");
            return;
        }

        if (endDateTime <= startDateTime) {
            toast.error("End date and time must be after the start date and time!");
            return;
        }

        const formattedDate2 = endDate
            ? endDate.toLocaleDateString("en-GB")
            : "No date selected";
        const formattedTime2 =
            endHour && endMinute
                ? `${endHour}:${endMinute}`
                : "No time selected";

        const formattedDate1 = startDate
            ? startDate.toLocaleDateString("en-GB")
            : "No date selected";
        const formattedTime1 =
            startHour && startMinute
                ? `${startHour}:${startMinute}`
                : "No time selected";

        onSave({
                startDate: formattedDate1,
                startTime: formattedTime1,
                endDate: formattedDate2,
                endTime: formattedTime2,
        });

        handleClose();
    }

    return (
        <Dialog
            onClose={handleClose}
            open={open}
            className="schedule-interval-dialog"
            maxWidth="sm"
            fullWidth
        >
            <DialogTitle className="schedule-interval-dialog-title">Schedule Interval of Activation</DialogTitle>
            <Form method="post" onSubmit={handleSubmit}>
                <DialogContent className="schedule-interval-dialog-content">
                    <div className="schedule-interval-dialog-fields">
                        <div className="schedule-interval-dialog-field-item">
                            <label htmlFor="startDate">Start Date:</label>
                            <DatePicker
                                selected={startDate}
                                onChange={(date) => setStartDate(date)}
                                className="schedule-interval-dialog-datepicker"
                                dateFormat="dd/MM/yyyy"
                                placeholderText="Select start date"
                                isClearable
                            />
                        </div>
                        <div className="schedule-interval-dialog-field-item">
                            <label>Start Time:</label>
                            <div className="schedule-interval-dialog-time-container">
                                <select
                                    value={startHour}
                                    onChange={(e) => setStartHour(e.target.value)}
                                    className="schedule-interval-dialog-timepicker"
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
                                    className="schedule-interval-dialog-timepicker"
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
                        <div className="schedule-interval-dialog-field-item">
                            <label htmlFor="endDate">End Date:</label>
                            <DatePicker
                                selected={endDate}
                                onChange={(date) => setEndDate(date)}
                                className="schedule-interval-dialog-datepicker"
                                dateFormat="dd/MM/yyyy"
                                placeholderText="Select end date"
                                isClearable
                            />
                        </div>
                        <div className="schedule-interval-dialog-field-item">
                            <label>End Time:</label>
                            <div className="schedule-interval-dialog-time-container">
                                <select
                                    value={endHour}
                                    onChange={(e) => setEndHour(e.target.value)}
                                    className="schedule-interval-dialog-timepicker"
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
                                    value={endMinute}
                                    onChange={(e) => setEndMinute(e.target.value)}
                                    className="schedule-interval-dialog-timepicker"
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
                <DialogActions className="schedule-interval-dialog-actions">
                    <button className="schedule-interval-dialog-cancel-btn" onClick={handleClose}>
                        Cancel
                    </button>
                    <button type="submit" className="schedule-interval-dialog-save-btn">
                        Save
                    </button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default ScheduleIntervalDialog;

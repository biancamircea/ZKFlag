import React, { useState } from 'react';
import { toast } from 'react-toastify';
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    TextField,
    Button
} from '@mui/material';
import { Form } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import './styles/ScheduleIntervalDialog.css';
import { scheduleToggle } from '../../../api/featureToggleApi';

function ScheduleIntervalDialog({ onClose, open, onSave, projectId, toggleId, instanceId, environmentName }) {
    const [startDate, setStartDate] = useState(null);
    const [startHour, setStartHour] = useState("");
    const [startMinute, setStartMinute] = useState("");
    const [endDate, setEndDate] = useState(null);
    const [endHour, setEndHour] = useState("");
    const [endMinute, setEndMinute] = useState("");
    const [recurrence, setRecurrence] = useState("ONE_TIME");

    const createDateTime = (date, hours, minutes) => {
        if (!date || hours === "" || minutes === "") return null;

        const newDate = new Date(date);
        newDate.setHours(parseInt(hours, 10));
        newDate.setMinutes(parseInt(minutes, 10));
        newDate.setSeconds(0);
        newDate.setMilliseconds(0);

        return newDate;
    };

    const handleClose = () => {
        onClose();
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!projectId || !toggleId || !instanceId || !environmentName) {
            toast.error('Missing required parameters');
            return;
        }

        const activateAt = new Date(startDate);
        activateAt.setHours(parseInt(startHour), parseInt(startMinute), 0, 0);

        const deactivateAt = new Date(endDate);
        deactivateAt.setHours(parseInt(endHour), parseInt(endMinute), 0, 0);

        try {
            await scheduleToggle(
                projectId,
                toggleId,
                instanceId,
                environmentName,
                activateAt,
                deactivateAt,
                recurrence
            );
            toast.success("Schedule created successfully!");
            onSave();
            handleClose();
        } catch (error) {
            console.error('Error:', error);
            toast.error(`Failed to create schedule: ${error.message}`);
        }
    };

    return (
        <Dialog
            onClose={handleClose}
            open={open}
            className="schedule-interval-dialog"
            maxWidth="sm"
            fullWidth
        >
            <DialogTitle className="schedule-interval-dialog-title">
                Schedule Interval of Activation
            </DialogTitle>
            <Form method="post" onSubmit={handleSubmit}>
                <DialogContent className="schedule-interval-dialog-content">
                    <div className="schedule-interval-dialog-fields">
                        <FormControl fullWidth size="small" sx={{ mb: 2 }}>
                            <InputLabel id="recurrence-label">Recurrence</InputLabel>
                            <Select
                                labelId="recurrence-label"
                                id="recurrence-select"
                                value={recurrence}
                                label="Recurrence"
                                onChange={(e) => setRecurrence(e.target.value)}
                            >
                                <MenuItem value="ONE_TIME">One Time</MenuItem>
                                <MenuItem value="DAILY">Daily</MenuItem>
                                <MenuItem value="WEEKLY">Weekly</MenuItem>
                                <MenuItem value="MONTHLY">Monthly</MenuItem>
                            </Select>
                        </FormControl>

                        <FormControl fullWidth size="small" sx={{ mb: 2 }}>
                            <InputLabel shrink htmlFor="start-date">
                                Start Date
                            </InputLabel>
                            <DatePicker
                                selected={startDate}
                                onChange={(date) => setStartDate(date)}
                                className="schedule-interval-dialog-datepicker"
                                dateFormat="dd/MM/yyyy"
                                placeholderText="Select start date"
                                minDate={new Date()}
                                isClearable
                                customInput={
                                    <TextField
                                        fullWidth
                                        size="small"
                                        id="start-date"
                                        sx={{ mt: 1 }}
                                    />
                                }
                            />
                        </FormControl>

                        <FormControl fullWidth size="small" sx={{ mb: 2 }}>
                            <InputLabel shrink id="start-hour-label">Start Time</InputLabel>
                            <div className="time-select-container">
                                <Select
                                    labelId="start-hour-label"
                                    id="start-hour"
                                    value={startHour}
                                    label="Hour"
                                    onChange={(e) => setStartHour(e.target.value)}
                                    size="small"
                                    sx={{ width: '48%', mr: '4%' }}
                                >
                                    <MenuItem value="">HH</MenuItem>
                                    {Array.from({ length: 24 }, (_, i) => (
                                        <MenuItem
                                            key={i}
                                            value={i.toString().padStart(2, '0')}
                                        >
                                            {i.toString().padStart(2, '0')}
                                        </MenuItem>
                                    ))}
                                </Select>
                                <Select
                                    id="start-minute"
                                    value={startMinute}
                                    label="Minute"
                                    onChange={(e) => setStartMinute(e.target.value)}
                                    size="small"
                                    sx={{ width: '48%' }}
                                >
                                    <MenuItem value="">MM</MenuItem>
                                    {Array.from({ length: 60 }, (_, i) => (
                                        <MenuItem
                                            key={i}
                                            value={i.toString().padStart(2, '0')}
                                        >
                                            {i.toString().padStart(2, '0')}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </div>
                        </FormControl>

                        <FormControl fullWidth size="small" sx={{ mb: 2 }}>
                            <InputLabel shrink htmlFor="end-date">
                                End Date
                            </InputLabel>
                            <DatePicker
                                selected={endDate}
                                onChange={(date) => setEndDate(date)}
                                className="schedule-interval-dialog-datepicker"
                                dateFormat="dd/MM/yyyy"
                                placeholderText="Select end date"
                                minDate={startDate || new Date()}
                                isClearable
                                customInput={
                                    <TextField
                                        fullWidth
                                        size="small"
                                        id="end-date"
                                        sx={{ mt: 1 }}
                                    />
                                }
                            />
                        </FormControl>

                        <FormControl fullWidth size="small" sx={{ mb: 2 }}>
                            <InputLabel shrink id="end-hour-label">End Time</InputLabel>
                            <div className="time-select-container">
                                <Select
                                    labelId="end-hour-label"
                                    id="end-hour"
                                    value={endHour}
                                    label="Hour"
                                    onChange={(e) => setEndHour(e.target.value)}
                                    size="small"
                                    sx={{ width: '48%', mr: '4%' }}
                                >
                                    <MenuItem value="">HH</MenuItem>
                                    {Array.from({ length: 24 }, (_, i) => (
                                        <MenuItem
                                            key={i}
                                            value={i.toString().padStart(2, '0')}
                                        >
                                            {i.toString().padStart(2, '0')}
                                        </MenuItem>
                                    ))}
                                </Select>
                                <Select
                                    id="end-minute"
                                    value={endMinute}
                                    label="Minute"
                                    onChange={(e) => setEndMinute(e.target.value)}
                                    size="small"
                                    sx={{ width: '48%' }}
                                >
                                    <MenuItem value="">MM</MenuItem>
                                    {Array.from({ length: 60 }, (_, i) => (
                                        <MenuItem
                                            key={i}
                                            value={i.toString().padStart(2, '0')}
                                        >
                                            {i.toString().padStart(2, '0')}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </div>
                        </FormControl>
                    </div>
                </DialogContent>
                <DialogActions>
                    <button className="schedule-dialog-cancel-btn" onClick={(event) => { event.preventDefault(); handleClose(); }}>
                        Cancel
                    </button>
                    <button className="schedule-dialog-save-btn" type="submit">Save</button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default ScheduleIntervalDialog;
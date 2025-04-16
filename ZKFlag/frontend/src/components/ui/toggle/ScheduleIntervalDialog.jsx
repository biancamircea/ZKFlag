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
    Button,
    Checkbox,
    FormControlLabel
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
    const [wantsDeactivation, setWantsDeactivation] = useState(false);

    const handleClose = () => {
        onClose();
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!projectId || !toggleId || !instanceId || !environmentName) {
            toast.error('Missing required parameters');
            return;
        }

        const isStartComplete = startDate && startHour !== "" && startMinute !== "";
        const isEndComplete = endDate && endHour !== "" && endMinute !== "";

        let activateAt = null;
        let deactivateAt = null;

        if (recurrence === "ONE_TIME") {
            if (!isStartComplete && !isEndComplete) {
                toast.error("Please fill in at least a start date+time or an end date+time.");
                return;
            }
            activateAt = isStartComplete
                ? new Date(new Date(startDate).setHours(parseInt(startHour), parseInt(startMinute), 0, 0))
                : null;
            deactivateAt = isEndComplete
                ? new Date(new Date(endDate).setHours(parseInt(endHour), parseInt(endMinute), 0, 0))
                : null;
        } else {
            if (!isStartComplete) {
                toast.error("Start date and time are required for recurring schedules.");
                return;
            }

            activateAt = new Date(new Date(startDate).setHours(parseInt(startHour), parseInt(startMinute), 0, 0));

            if (wantsDeactivation && isEndComplete) {
                deactivateAt = new Date(new Date(endDate).setHours(parseInt(endHour), parseInt(endMinute), 0, 0));
            }
        }

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

    const showEndFields = recurrence === "ONE_TIME" || wantsDeactivation;
    const showDeactivationCheckbox = recurrence !== "ONE_TIME";

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
                                onChange={(e) => {
                                    setRecurrence(e.target.value);
                                    setWantsDeactivation(false);
                                    setEndDate(null);
                                    setEndHour("");
                                    setEndMinute("");
                                }}
                            >
                                <MenuItem value="ONE_TIME">One Time</MenuItem>
                                <MenuItem value="DAILY">Daily</MenuItem>
                                <MenuItem value="WEEKLY">Weekly</MenuItem>
                                <MenuItem value="MONTHLY">Monthly</MenuItem>
                            </Select>
                        </FormControl>

                        {/* Start Date */}
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

                        {/* Start Time */}
                        <FormControl fullWidth size="small" sx={{ mb: 2 }}>
                            <InputLabel shrink id="start-hour-label">Start Time</InputLabel>
                            <div className="time-select-container">
                                <Select
                                    labelId="start-hour-label"
                                    id="start-hour"
                                    value={startHour}
                                    onChange={(e) => setStartHour(e.target.value)}
                                    size="small"
                                    sx={{ width: '48%', mr: '4%' }}
                                >
                                    <MenuItem value="">HH</MenuItem>
                                    {Array.from({ length: 24 }, (_, i) => (
                                        <MenuItem key={i} value={i.toString().padStart(2, '0')}>
                                            {i.toString().padStart(2, '0')}
                                        </MenuItem>
                                    ))}
                                </Select>
                                <Select
                                    id="start-minute"
                                    value={startMinute}
                                    onChange={(e) => setStartMinute(e.target.value)}
                                    size="small"
                                    sx={{ width: '48%' }}
                                >
                                    <MenuItem value="">MM</MenuItem>
                                    {Array.from({ length: 60 }, (_, i) => (
                                        <MenuItem key={i} value={i.toString().padStart(2, '0')}>
                                            {i.toString().padStart(2, '0')}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </div>
                        </FormControl>

                        {showDeactivationCheckbox && (
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={wantsDeactivation}
                                        onChange={(e) => setWantsDeactivation(e.target.checked)}
                                    />
                                }
                                label="Include deactivation time"
                                sx={{ mb: 2 }}
                            />
                        )}

                        {showEndFields && (
                            <>
                                {/* End Date */}
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

                                {/* End Time */}
                                <FormControl fullWidth size="small" sx={{ mb: 2 }}>
                                    <InputLabel shrink id="end-hour-label">End Time</InputLabel>
                                    <div className="time-select-container">
                                        <Select
                                            labelId="end-hour-label"
                                            id="end-hour"
                                            value={endHour}
                                            onChange={(e) => setEndHour(e.target.value)}
                                            size="small"
                                            sx={{ width: '48%', mr: '4%' }}
                                        >
                                            <MenuItem value="">HH</MenuItem>
                                            {Array.from({ length: 24 }, (_, i) => (
                                                <MenuItem key={i} value={i.toString().padStart(2, '0')}>
                                                    {i.toString().padStart(2, '0')}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                        <Select
                                            id="end-minute"
                                            value={endMinute}
                                            onChange={(e) => setEndMinute(e.target.value)}
                                            size="small"
                                            sx={{ width: '48%' }}
                                        >
                                            <MenuItem value="">MM</MenuItem>
                                            {Array.from({ length: 60 }, (_, i) => (
                                                <MenuItem key={i} value={i.toString().padStart(2, '0')}>
                                                    {i.toString().padStart(2, '0')}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </div>
                                </FormControl>
                            </>
                        )}
                    </div>
                </DialogContent>
                <DialogActions>
                    <button className="schedule-dialog-cancel-btn" onClick={(e) => { e.preventDefault(); handleClose(); }}>
                        Cancel
                    </button>
                    <button className="schedule-dialog-save-btn" type="submit">Save</button>
                </DialogActions>
            </Form>
        </Dialog>
    );
}

export default ScheduleIntervalDialog;

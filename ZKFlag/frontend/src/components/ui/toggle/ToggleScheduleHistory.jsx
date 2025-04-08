function ToggleScheduleHistory({ history, environmentName }) {
    if (!history || history.length === 0) {
        return (
            <div className="toggle-environment-constraints-container">
                <div className="header">
                    <img src="/images/environment.png" alt="Environment" className="environment-icon" />
                    <h3  className="history">{environmentName} - History</h3>
                </div>
                <div className="schedule-status-container">
                    <p className="no-strategy-message">No history available</p>
                </div>
            </div>
        );
    }

    const formatDateTime = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleString();
    };

    return (
        <div className="toggle-environment-constraints-container">
            <div className="header">
                <img src="/images/environment.png" alt="Environment" className="environment-icon" />
                <h3 className="history" >{environmentName} - History</h3>
            </div>

            <div className="schedule-status-container">
                <div className="strategy-pairs-container">
                    {history.map((item) => (
                        <div key={item.id} className="strategy-pair" style={{ display: "flex", justifyContent: "space-between", alignItems: "center", width: "100%", margin:"0px" }}>
                            <div style={{ flex: "1", display: "flex", alignItems: "center", margin:"0px" }}>
                                <p className="history">{item.scheduleType}</p>
                            </div>
                            <div style={{ flex: "1", display: "flex", alignItems: "center", margin:"0px"  }}>
                                <p className="history">Activation: {formatDateTime(item.activateAt)}</p>
                            </div>
                            <div style={{ flex: "1", display: "flex", alignItems: "center", margin:"0px"  }}>
                                <p className="history">Deactivation: {formatDateTime(item.deactivateAt)}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default ToggleScheduleHistory;
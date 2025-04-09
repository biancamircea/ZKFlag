import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import React from "react";

const COLORS = ['#0088FE', '#FF8042'];

function FeatureToggleInstanceStatistics({ statistics, index }) {
    const hasData = statistics.trueCount > 0 || statistics.falseCount > 0;

    const data = hasData
        ? [
            { name: 'True', value: statistics.trueCount },
            { name: 'False', value: statistics.falseCount }
        ]
        : [
            { name: 'No data', value: 1 }
        ];

    return (
        <div className="feature-toggle-overview-meta-card" style={{marginBottom:"20px"}}>
            {index === 0 && <h3 className="statistics-title">Toggle Evaluation Statistics</h3>}
            <h4 style={{marginTop:"20px"}}>{statistics.environmentName}</h4>
            <div style={{ width: '100%', height: 250 }}>
                <ResponsiveContainer>
                    <PieChart>
                        <Pie
                            data={data}
                            cx="50%"
                            cy="50%"
                            labelLine={false}
                            outerRadius={80}
                            fill="#8884d8"
                            dataKey="value"
                            label={({ name, percent }) =>
                                hasData
                                    ? `${name}: ${(percent * 100).toFixed(0)}%`
                                    : name
                            }
                        >
                            {data.map((entry, index) => (
                                <Cell
                                    key={`cell-${index}`}
                                    fill={hasData ? COLORS[index % COLORS.length] : '#CCCCCC'}
                                />
                            ))}
                        </Pie>
                        {hasData && <Tooltip />}
                        {hasData && <Legend />}
                    </PieChart>
                </ResponsiveContainer>
            </div>
            <p>Total evaluations: {statistics.trueCount + statistics.falseCount}</p>
        </div>
    );
}

export default FeatureToggleInstanceStatistics;
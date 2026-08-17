import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

function ContributionChart({ contributions }) {
    const data = contributions.map((c) => ({
        name: c.username,
        '메시지': c.messageCount,
        '멘션': c.mentionCount,
        'Task 생성': c.taskCreatedCount,
        'Task 완료': c.taskCompletedCount,
    }));

    return (
        <ResponsiveContainer width="100%" height={320}>
            <BarChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Bar dataKey="메시지" fill="#4f46e5" />
                <Bar dataKey="멘션" fill="#7c3aed" />
                <Bar dataKey="Task 생성" fill="#f6ad55" />
                <Bar dataKey="Task 완료" fill="#48bb78" />
            </BarChart>
        </ResponsiveContainer>
    );
}

export default ContributionChart;
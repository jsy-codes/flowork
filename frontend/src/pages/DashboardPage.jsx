import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getContributions, getRoomHealth, exportCsv } from '../api/contribution';
import ContributionChart from '../components/dashboard/ContributionChart';

function DashboardPage() {
    const { roomId } = useParams();
    const navigate = useNavigate();
    const [contributions, setContributions] = useState([]);
    const [health, setHealth] = useState(null);

    useEffect(() => {
        getContributions(roomId).then(({ data }) => setContributions(data));
        getRoomHealth(roomId).then(({ data }) => setHealth(data));
    }, [roomId]);

    const handleExport = async () => {
        const { data } = await exportCsv(roomId);
        const url = URL.createObjectURL(new Blob([data]));
        const a = document.createElement('a');
        a.href = url;
        a.download = `room-${roomId}-contributions.csv`;
        a.click();
    };

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <button style={styles.backBtn} onClick={() => navigate('/chat')}>← 채팅으로</button>
                <h2 style={styles.title}>팀 기여 대시보드</h2>
                <button style={styles.exportBtn} onClick={handleExport}>CSV 내보내기</button>
            </div>

            {/* 팀 건강도 */}
            {health && (
                <div style={styles.healthGrid}>
                    <HealthCard label="전체 메시지" value={health.totalMessages} />
                    <HealthCard label="전체 Task" value={health.totalTasks} />
                    <HealthCard label="완료 Task" value={health.completedTasks} />
                    <HealthCard label="완료율" value={`${health.taskCompletionRate}%`} />
                    <HealthCard label="평균 완료 시간" value={health.avgTaskMinutes ? `${Math.round(health.avgTaskMinutes)}분` : '-'} />
                    <HealthCard label="활동 멤버" value={health.activeMemberCount} />
                </div>
            )}

            {/* 기여 차트 */}
            <div style={styles.chartBox}>
                <h3 style={styles.sectionTitle}>멤버별 기여 breakdown</h3>
                {contributions.length > 0
                    ? <ContributionChart contributions={contributions} />
                    : <p style={{ color: '#aaa' }}>데이터가 없습니다.</p>}
            </div>

            {/* 멤버별 상세 */}
            <div style={styles.tableBox}>
                <h3 style={styles.sectionTitle}>멤버별 상세</h3>
                <table style={styles.table}>
                    <thead>
                    <tr style={styles.th}>
                        {['멤버', '메시지', '멘션', 'Task 생성', 'Task 완료', '평균 완료'].map((h) => (
                            <th key={h} style={styles.thCell}>{h}</th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {contributions.map((c) => (
                        <tr key={c.userId} style={styles.tr}>
                            <td style={styles.td}>{c.username}</td>
                            <td style={styles.td}>{c.messageCount}</td>
                            <td style={styles.td}>{c.mentionCount}</td>
                            <td style={styles.td}>{c.taskCreatedCount}</td>
                            <td style={styles.td}>{c.taskCompletedCount}</td>
                            <td style={styles.td}>{c.avgCompletionMinutes ? `${Math.round(c.avgCompletionMinutes)}분` : '-'}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function HealthCard({ label, value }) {
    return (
        <div style={cardStyles.card}>
            <div style={cardStyles.value}>{value}</div>
            <div style={cardStyles.label}>{label}</div>
        </div>
    );
}

const styles = {
    container: { padding: '24px', maxWidth: '1000px', margin: '0 auto' },
    header: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' },
    title: { fontSize: '22px', fontWeight: 'bold', margin: 0 },
    backBtn: { padding: '8px 16px', background: 'transparent', border: '1px solid #ddd', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' },
    exportBtn: { padding: '8px 16px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' },
    healthGrid: { display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px', marginBottom: '32px' },
    chartBox: { background: '#fff', borderRadius: '12px', padding: '24px', border: '1px solid #eee', marginBottom: '24px' },
    tableBox: { background: '#fff', borderRadius: '12px', padding: '24px', border: '1px solid #eee' },
    sectionTitle: { margin: '0 0 16px', fontSize: '16px', fontWeight: 'bold' },
    table: { width: '100%', borderCollapse: 'collapse' },
    th: { background: '#f5f5f5' },
    thCell: { padding: '10px 12px', textAlign: 'left', fontSize: '13px', color: '#666' },
    tr: { borderBottom: '1px solid #eee' },
    td: { padding: '10px 12px', fontSize: '14px' },
};

const cardStyles = {
    card: { background: '#fff', border: '1px solid #eee', borderRadius: '12px', padding: '20px', textAlign: 'center' },
    value: { fontSize: '28px', fontWeight: 'bold', color: '#4f46e5' },
    label: { fontSize: '12px', color: '#888', marginTop: '4px' },
};

export default DashboardPage;
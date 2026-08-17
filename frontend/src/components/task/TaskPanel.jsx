import { useEffect, useState } from 'react';
import { getRoomTasks, createTask, updateTaskStatus } from '../../api/task';

function TaskPanel({ roomId }) {
    const [tasks, setTasks] = useState([]);
    const [showCreate, setShowCreate] = useState(false);
    const [form, setForm] = useState({ title: '', assigneeId: '' });

    useEffect(() => {
        if (!roomId) return;
        getRoomTasks(roomId).then(({ data }) => setTasks(data));
    }, [roomId]);

    const handleCreate = async () => {
        if (!form.title.trim() || !form.assigneeId) return;
        const { data } = await createTask({ ...form, assigneeId: Number(form.assigneeId) });
        setTasks([...tasks, data]);
        setForm({ title: '', assigneeId: '' });
        setShowCreate(false);
    };

    const handleStatusChange = async (taskId, status) => {
        const { data } = await updateTaskStatus(taskId, status);
        setTasks(tasks.map((t) => (t.id === taskId ? data : t)));
    };

    const statusGroups = {
        PENDING: tasks.filter((t) => t.status === 'PENDING'),
        IN_PROGRESS: tasks.filter((t) => t.status === 'IN_PROGRESS'),
        COMPLETED: tasks.filter((t) => t.status === 'COMPLETED'),
    };

    const statusLabel = { PENDING: '대기 중', IN_PROGRESS: '진행 중', COMPLETED: '완료' };
    const statusColor = { PENDING: '#f6ad55', IN_PROGRESS: '#4299e1', COMPLETED: '#48bb78' };

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <span style={styles.title}>Task</span>
                <button style={styles.addBtn} onClick={() => setShowCreate(!showCreate)}>+ 추가</button>
            </div>

            {showCreate && (
                <div style={styles.createBox}>
                    <input style={styles.input} placeholder="Task 제목" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
                    <input style={styles.input} placeholder="담당자 ID" value={form.assigneeId} onChange={(e) => setForm({ ...form, assigneeId: e.target.value })} />
                    <button style={styles.createBtn} onClick={handleCreate}>생성</button>
                </div>
            )}

            {Object.entries(statusGroups).map(([status, items]) => (
                <div key={status} style={styles.group}>
                    <div style={{ ...styles.groupLabel, color: statusColor[status] }}>{statusLabel[status]} ({items.length})</div>
                    {items.map((task) => (
                        <div key={task.id} style={styles.taskItem}>
                            <span style={styles.taskTitle}>{task.title}</span>
                            <span style={styles.assignee}>@{task.assigneeUsername}</span>
                            <select
                                style={styles.select}
                                value={task.status}
                                onChange={(e) => handleStatusChange(task.id, e.target.value)}
                            >
                                <option value="PENDING">대기 중</option>
                                <option value="IN_PROGRESS">진행 중</option>
                                <option value="COMPLETED">완료</option>
                            </select>
                        </div>
                    ))}
                </div>
            ))}
        </div>
    );
}

const styles = {
    container: { width: '280px', borderLeft: '1px solid #eee', height: '100vh', overflowY: 'auto', padding: '16px' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' },
    title: { fontWeight: 'bold', fontSize: '15px' },
    addBtn: { background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '6px', padding: '6px 12px', cursor: 'pointer', fontSize: '12px' },
    createBox: { background: '#f9f9f9', borderRadius: '8px', padding: '12px', marginBottom: '16px' },
    input: { display: 'block', width: '100%', padding: '8px', marginBottom: '8px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '13px', boxSizing: 'border-box' },
    createBtn: { width: '100%', padding: '8px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '13px' },
    group: { marginBottom: '16px' },
    groupLabel: { fontWeight: 'bold', fontSize: '12px', marginBottom: '8px', textTransform: 'uppercase' },
    taskItem: { background: '#f9f9f9', borderRadius: '8px', padding: '10px', marginBottom: '6px' },
    taskTitle: { display: 'block', fontSize: '13px', fontWeight: '500', marginBottom: '4px' },
    assignee: { fontSize: '11px', color: '#888' },
    select: { display: 'block', width: '100%', marginTop: '6px', padding: '4px', borderRadius: '4px', border: '1px solid #ddd', fontSize: '12px' },
};

export default TaskPanel;
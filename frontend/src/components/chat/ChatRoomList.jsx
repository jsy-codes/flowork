import { useEffect, useState } from 'react';
import { getRooms, createRoom } from '../../api/chat';
import useChatStore from '../../store/chatStore';

function ChatRoomList({ onSelectRoom }) {
    const { rooms, setRooms, currentRoom } = useChatStore();
    const [showCreate, setShowCreate] = useState(false);
    const [roomName, setRoomName] = useState('');

    useEffect(() => {
        getRooms().then(({ data }) => setRooms(data));
    }, []);

    const handleCreate = async () => {
        if (!roomName.trim()) return;
        const { data } = await createRoom({ name: roomName, type: 'GROUP' });
        setRooms([...rooms, data]);
        setRoomName('');
        setShowCreate(false);
    };

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <span style={styles.headerTitle}>채팅방</span>
                <button style={styles.addBtn} onClick={() => setShowCreate(!showCreate)}>+</button>
            </div>

            {showCreate && (
                <div style={styles.createBox}>
                    <input
                        style={styles.input}
                        placeholder="채팅방 이름"
                        value={roomName}
                        onChange={(e) => setRoomName(e.target.value)}
                    />
                    <button style={styles.createBtn} onClick={handleCreate}>생성</button>
                </div>
            )}

            {rooms.map((room) => (
                <div
                    key={room.id}
                    style={{ ...styles.roomItem, background: currentRoom?.id === room.id ? '#e8e8ff' : 'transparent' }}
                    onClick={() => onSelectRoom(room)}
                >
                    # {room.name}
                </div>
            ))}
        </div>
    );
}

const styles = {
    container: { width: '220px', background: '#1a1a2e', color: '#fff', height: '100vh', display: 'flex', flexDirection: 'column' },
    header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '20px 16px 12px' },
    headerTitle: { fontWeight: 'bold', fontSize: '15px' },
    addBtn: { background: 'none', border: 'none', color: '#fff', fontSize: '20px', cursor: 'pointer' },
    createBox: { padding: '0 12px 12px' },
    input: { width: '100%', padding: '8px', borderRadius: '6px', border: 'none', fontSize: '13px', boxSizing: 'border-box', marginBottom: '6px' },
    createBtn: { width: '100%', padding: '8px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '13px' },
    roomItem: { padding: '10px 16px', cursor: 'pointer', fontSize: '14px', borderRadius: '6px', margin: '2px 8px' },
};

export default ChatRoomList;
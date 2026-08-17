import { useEffect, useRef } from 'react';
import useChatStore from '../../store/chatStore';
import { getMessages } from '../../api/chat';
import MessageInput from './MessageInput';
import useWebSocket from '../../hooks/useWebSocket';

function ChatWindow({ userId }) {
    const { currentRoom, messages, setMessages, notifications } = useChatStore();
    const bottomRef = useRef(null);
    const { sendMessage } = useWebSocket(currentRoom?.id, userId);

    useEffect(() => {
        if (!currentRoom) return;
        getMessages(currentRoom.id).then(({ data }) => {
            setMessages([...data.content].reverse()); // 최신순 → 오래된순
        });
    }, [currentRoom]);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    if (!currentRoom) {
        return (
            <div style={styles.empty}>
                <p>채팅방을 선택해주세요</p>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            {/* 헤더 */}
            <div style={styles.header}>
                <span style={styles.roomName}># {currentRoom.name}</span>
                {notifications.length > 0 && (
                    <span style={styles.notiBadge}>🔔 {notifications[0]}</span>
                )}
            </div>

            {/* 메시지 목록 */}
            <div style={styles.messageList}>
                {messages.map((msg) => (
                    <div key={msg.id} style={styles.messageItem}>
                        <span style={styles.sender}>{msg.senderUsername}</span>
                        <span style={styles.content}>{msg.content}</span>
                        <span style={styles.time}>
              {new Date(msg.createdAt).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}
            </span>
                    </div>
                ))}
                <div ref={bottomRef} />
            </div>

            {/* 입력창 */}
            <MessageInput onSend={(content) => sendMessage(currentRoom.id, content)} />
        </div>
    );
}

const styles = {
    container: { flex: 1, display: 'flex', flexDirection: 'column', height: '100vh' },
    empty: { flex: 1, display: 'flex', justifyContent: 'center', alignItems: 'center', color: '#aaa' },
    header: { padding: '16px 20px', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    roomName: { fontWeight: 'bold', fontSize: '16px' },
    notiBadge: { fontSize: '12px', color: '#4f46e5', maxWidth: '300px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' },
    messageList: { flex: 1, overflowY: 'auto', padding: '16px 20px', display: 'flex', flexDirection: 'column', gap: '8px' },
    messageItem: { display: 'flex', alignItems: 'baseline', gap: '8px' },
    sender: { fontWeight: 'bold', fontSize: '13px', color: '#4f46e5', minWidth: '80px' },
    content: { fontSize: '14px', flex: 1 },
    time: { fontSize: '11px', color: '#aaa', whiteSpace: 'nowrap' },
};

export default ChatWindow;
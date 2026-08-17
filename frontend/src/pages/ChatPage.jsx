import { useNavigate } from 'react-router-dom';
import ChatRoomList from '../components/chat/ChatRoomList';
import ChatWindow from '../components/chat/ChatWindow';
import TaskPanel from '../components/task/TaskPanel';
import useChatStore from '../store/chatStore';
import useAuthStore from '../store/authStore';

// JWT에서 userId 파싱
const parseUserId = (token) => {
    try {
        return JSON.parse(atob(token.split('.')[1])).sub;
    } catch {
        return null;
    }
};

function ChatPage() {
    const navigate = useNavigate();
    const { currentRoom, setCurrentRoom } = useChatStore();
    const { accessToken, logout } = useAuthStore();
    const userId = parseUserId(accessToken);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div style={styles.container}>
            {/* 사이드바 */}
            <div style={styles.sidebar}>
                <ChatRoomList onSelectRoom={setCurrentRoom} />
                <div style={styles.sidebarBottom}>
                    {currentRoom && (
                        <button style={styles.dashBtn} onClick={() => navigate(`/dashboard/${currentRoom.id}`)}>
                            📊 기여 대시보드
                        </button>
                    )}
                    <button style={styles.logoutBtn} onClick={handleLogout}>로그아웃</button>
                </div>
            </div>

            {/* 채팅 영역 */}
            <ChatWindow userId={userId} />

            {/* Task 패널 */}
            {currentRoom && <TaskPanel roomId={currentRoom.id} />}
        </div>
    );
}

const styles = {
    container: { display: 'flex', height: '100vh' },
    sidebar: { display: 'flex', flexDirection: 'column', justifyContent: 'space-between', background: '#1a1a2e' },
    sidebarBottom: { padding: '12px', display: 'flex', flexDirection: 'column', gap: '8px' },
    dashBtn: { padding: '10px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' },
    logoutBtn: { padding: '10px', background: 'transparent', color: '#aaa', border: '1px solid #444', borderRadius: '8px', cursor: 'pointer', fontSize: '13px' },
};

export default ChatPage;
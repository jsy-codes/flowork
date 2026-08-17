import { useState } from 'react';

function MessageInput({ onSend }) {
    const [content, setContent] = useState('');

    const handleSend = () => {
        if (!content.trim()) return;
        onSend(content);
        setContent('');
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
        <div style={styles.container}>
            <input
                style={styles.input}
                placeholder="메시지 입력... (@username 으로 멘션)"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                onKeyDown={handleKeyDown}
            />
            <button style={styles.button} onClick={handleSend}>전송</button>
        </div>
    );
}

const styles = {
    container: { display: 'flex', gap: '8px', padding: '16px', borderTop: '1px solid #eee' },
    input: { flex: 1, padding: '12px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px' },
    button: { padding: '12px 20px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px' },
};

export default MessageInput;
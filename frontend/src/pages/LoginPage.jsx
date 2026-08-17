import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../api/auth';
import useAuthStore from '../store/authStore';

function LoginPage() {
    const navigate = useNavigate();
    const { login: setAuth } = useAuthStore();
    const [form, setForm] = useState({ email: '', password: '' });
    const [error, setError] = useState('');

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async () => {
        try {
            const { data } = await login(form);
            setAuth(data.accessToken, data.refreshToken, null);
            navigate('/chat');
        } catch {
            setError('이메일 또는 비밀번호가 올바르지 않습니다.');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>Flowork</h1>
                <p style={styles.subtitle}>팀 기여를 측정하는 협업 플랫폼</p>

                {error && <p style={styles.error}>{error}</p>}

                <input
                    style={styles.input}
                    name="email"
                    type="email"
                    placeholder="이메일"
                    value={form.email}
                    onChange={handleChange}
                />
                <input
                    style={styles.input}
                    name="password"
                    type="password"
                    placeholder="비밀번호"
                    value={form.password}
                    onChange={handleChange}
                />
                <button style={styles.button} onClick={handleSubmit}>로그인</button>

                <p style={styles.link}>
                    계정이 없으신가요? <Link to="/signup">회원가입</Link>
                </p>
            </div>
        </div>
    );
}

const styles = {
    container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#f0f2f5' },
    card: { background: '#fff', padding: '40px', borderRadius: '12px', width: '360px', boxShadow: '0 2px 12px rgba(0,0,0,0.1)' },
    title: { margin: 0, fontSize: '28px', color: '#1a1a2e' },
    subtitle: { color: '#888', marginBottom: '24px', fontSize: '13px' },
    input: { display: 'block', width: '100%', padding: '12px', marginBottom: '12px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' },
    button: { width: '100%', padding: '12px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', fontSize: '15px', cursor: 'pointer' },
    error: { color: '#e53e3e', fontSize: '13px', marginBottom: '12px' },
    link: { textAlign: 'center', marginTop: '16px', fontSize: '13px' },
};

export default LoginPage;
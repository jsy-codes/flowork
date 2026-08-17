import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { signup } from '../api/auth';

function SignupPage() {
    const navigate = useNavigate();
    const [form, setForm] = useState({ username: '', email: '', password: '' });
    const [error, setError] = useState('');

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async () => {
        try {
            await signup(form);
            navigate('/login');
        } catch (err) {
            setError(err.response?.data?.message || '회원가입에 실패했습니다.');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h1 style={styles.title}>회원가입</h1>

                {error && <p style={styles.error}>{error}</p>}

                <input style={styles.input} name="username" placeholder="사용자 이름" value={form.username} onChange={handleChange} />
                <input style={styles.input} name="email" type="email" placeholder="이메일" value={form.email} onChange={handleChange} />
                <input style={styles.input} name="password" type="password" placeholder="비밀번호" value={form.password} onChange={handleChange} />
                <button style={styles.button} onClick={handleSubmit}>가입하기</button>

                <p style={styles.link}>
                    이미 계정이 있으신가요? <Link to="/login">로그인</Link>
                </p>
            </div>
        </div>
    );
}

const styles = {
    container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#f0f2f5' },
    card: { background: '#fff', padding: '40px', borderRadius: '12px', width: '360px', boxShadow: '0 2px 12px rgba(0,0,0,0.1)' },
    title: { margin: '0 0 24px', fontSize: '24px', color: '#1a1a2e' },
    input: { display: 'block', width: '100%', padding: '12px', marginBottom: '12px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px', boxSizing: 'border-box' },
    button: { width: '100%', padding: '12px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', fontSize: '15px', cursor: 'pointer' },
    error: { color: '#e53e3e', fontSize: '13px', marginBottom: '12px' },
    link: { textAlign: 'center', marginTop: '16px', fontSize: '13px' },
};

export default SignupPage;
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
});

// 요청마다 토큰 자동 첨부
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

// 401 시 refresh 재시도
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const original = error.config;
        if (error.response?.status === 401 && !original._retry) {
            original._retry = true;
            try {
                const refreshToken = localStorage.getItem('refreshToken');
                const { data } = await axios.post('http://localhost:8080/api/auth/refresh', null, {
                    headers: { Authorization: `Bearer ${refreshToken}` },
                });
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                original.headers.Authorization = `Bearer ${data.accessToken}`;
                return api(original);
            } catch {
                localStorage.clear();
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;
import { create } from 'zustand';

const useAuthStore = create((set) => ({
    user: null,
    accessToken: localStorage.getItem('accessToken') || null,

    login: (accessToken, refreshToken, user) => {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        set({ accessToken, user });
    },

    logout: () => {
        localStorage.clear();
        set({ user: null, accessToken: null });
    },
}));

export default useAuthStore;